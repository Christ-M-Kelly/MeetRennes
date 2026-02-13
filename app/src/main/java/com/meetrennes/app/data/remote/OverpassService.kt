package com.meetrennes.app.data.remote

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.meetrennes.app.domain.CategorieLieu
import com.meetrennes.app.domain.Lieu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request



class OverpassService(private val client: OkHttpClient) {

    private val gson = Gson()

    // Rectangle englobant Rennes (sud, ouest, nord, est)
    private val bbox = "48.08,-1.73,48.14,-1.63"

    /**
     * Requête Overpass QL :
     * - node["tourism"] → musées, attractions, galeries...
     * - node["amenity"~"restaurant|bar|cafe"] → restaurants, bars
     * - node["leisure"="park"] → parcs
     * - node["historic"] → sites historiques
     * - node["shop"="marketplace"] → marchés
     *
     * On récupère uniquement les nodes (points) avec leurs tags.
     */
    private val query = """
        [out:json][timeout:15];
        (
          node["tourism"~"museum|attraction|gallery|artwork"]($bbox);
          node["amenity"~"restaurant|bar|cafe|marketplace"]($bbox);
          node["leisure"="park"]($bbox);
          node["historic"]($bbox);
          node["shop"="marketplace"]($bbox);
        );
        out body;
    """.trimIndent()

    /**
     * Appelle l'API Overpass et retourne une liste de Lieu.
     * Exécuté sur Dispatchers.IO (thread background).
     */
    suspend fun fetchLieux(): List<Lieu> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("OverpassService", "=== Début appel API Overpass ===")

            // POST avec FormBody — méthode fiable pour Overpass
            val formBody = FormBody.Builder()
                .add("data", query)
                .build()

            val request = Request.Builder()
                .url("https://overpass-api.de/api/interpreter")
                .post(formBody)
                .header("User-Agent", "MeetRennes/1.0")
                .build()

            val response = client.newCall(request).execute()

            android.util.Log.d("OverpassService", "HTTP code: ${response.code}")

            if (!response.isSuccessful) {
                android.util.Log.e("OverpassService", "Erreur HTTP: ${response.code} - ${response.message}")
                return@withContext emptyList()
            }

            val body = response.body?.string() ?: return@withContext emptyList()
            android.util.Log.d("OverpassService", "Body reçu: ${body.take(200)}...")

            val json = gson.fromJson(body, JsonObject::class.java)
            val elements = json.getAsJsonArray("elements") ?: return@withContext emptyList()

            android.util.Log.d("OverpassService", "Nombre d'éléments OSM: ${elements.size()}")

            val result = elements.mapNotNull { element ->
                val obj = element.asJsonObject
                val tags = obj.getAsJsonObject("tags") ?: return@mapNotNull null
                val name = tags.get("name")?.asString ?: return@mapNotNull null
                val lat = obj.get("lat")?.asDouble ?: return@mapNotNull null
                val lon = obj.get("lon")?.asDouble ?: return@mapNotNull null

                val categorie = mapCategorie(tags)
                val id = "osm_${obj.get("id")?.asLong ?: return@mapNotNull null}"

                val description = buildDescription(tags)
                val adresse = buildAdresse(tags)
                val imageUrl = tags.get("image")?.asString
                    ?: tags.get("wikimedia_commons")?.asString
                    ?: getWikipediaImage(tags)
                    ?: ""

                Lieu(
                    id = id,
                    nom = name,
                    description = description,
                    categorie = categorie,
                    adresse = adresse,
                    latitude = lat,
                    longitude = lon,
                    imageUrl = imageUrl
                )
            }

            android.util.Log.d("OverpassService", "=== Lieux mappés: ${result.size} ===")
            result
        } catch (e: Exception) {
            android.util.Log.e("OverpassService", "EXCEPTION: ${e.javaClass.simpleName} - ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Mappe les tags OSM vers notre enum CategorieLieu.
     */
    private fun mapCategorie(tags: JsonObject): CategorieLieu {
        // Vérifier d'abord les tags les plus spécifiques
        val tourism = tags.get("tourism")?.asString
        val amenity = tags.get("amenity")?.asString
        val historic = tags.get("historic")?.asString
        val leisure = tags.get("leisure")?.asString
        val shop = tags.get("shop")?.asString

        return when {
            tourism == "museum" || tourism == "gallery" -> CategorieLieu.Musee
            leisure == "park" -> CategorieLieu.Parc
            amenity == "restaurant" || amenity == "cafe" -> CategorieLieu.Restaurant
            amenity == "bar" || amenity == "pub" -> CategorieLieu.Bar
            amenity == "marketplace" || shop == "marketplace" -> CategorieLieu.Marche
            historic != null -> CategorieLieu.Monument
            tourism == "attraction" || tourism == "artwork" -> CategorieLieu.Monument
            else -> CategorieLieu.Monument
        }
    }

    /**
     * Construit une description à partir des tags disponibles.
     */
    private fun buildDescription(tags: JsonObject): String {
        val parts = mutableListOf<String>()

        tags.get("description")?.asString?.let { parts.add(it) }
        tags.get("description:fr")?.asString?.let { parts.add(it) }

        if (parts.isEmpty()) {
            // Construire une description basique à partir des tags
            tags.get("historic")?.asString?.let { parts.add("Site historique : $it") }
            tags.get("tourism")?.asString?.let { parts.add("Lieu touristique") }
            tags.get("cuisine")?.asString?.let { parts.add("Cuisine : $it") }
            tags.get("opening_hours")?.asString?.let { parts.add("Horaires : $it") }
        }

        return parts.joinToString(". ").ifEmpty { "Lieu à découvrir à Rennes." }
    }

    /**
     * Construit l'adresse à partir des tags OSM.
     */
    private fun buildAdresse(tags: JsonObject): String {
        val street = tags.get("addr:street")?.asString ?: ""
        val number = tags.get("addr:housenumber")?.asString ?: ""
        val postcode = tags.get("addr:postcode")?.asString ?: "35000"
        val city = tags.get("addr:city")?.asString ?: "Rennes"

        val fullStreet = listOf(number, street).filter { it.isNotBlank() }.joinToString(" ")
        return if (fullStreet.isNotBlank()) {
            "$fullStreet, $postcode $city"
        } else {
            "$postcode $city"
        }
    }

    /**
     * Tente de récupérer une URL d'image depuis le tag Wikipedia.
     */
    private fun getWikipediaImage(tags: JsonObject): String? {
        val wikidata = tags.get("wikidata")?.asString ?: return null
        // On pointe vers l'image Wikidata par défaut (si elle existe)
        return "https://commons.wikimedia.org/wiki/Special:FilePath/${wikidata}.jpg"
    }
}
