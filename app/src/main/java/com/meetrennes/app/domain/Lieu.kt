package com.meetrennes.app.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CategorieLieu {
    Monument,
    Parc,
    Restaurant,
    Musee,
    Bar,
    Marche;

    fun label(): String = when (this) {
        Monument -> "Monument"
        Parc -> "Parc"
        Restaurant -> "Restaurant"
        Musee -> "Musée"
        Bar -> "Bar"
        Marche -> "Marché"
    }
}

@Entity(tableName = "lieux")
data class Lieu(
    @PrimaryKey val id: String,
    val nom: String,
    val description: String,
    val categorie: CategorieLieu,
    val adresse: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String,
    val isFavorite: Boolean = false
) {
    companion object {
        val DEFAULT = Lieu(
            id = "lieu_1",
            nom = "Parlement de Bretagne",
            description = "Ancien siège du parlement de Bretagne, chef-d'œuvre du XVIIe siècle. Restauré après l'incendie de 1994, il abrite aujourd'hui la cour d'appel de Rennes.",
            categorie = CategorieLieu.Monument,
            adresse = "Place du Parlement de Bretagne, 35000 Rennes",
            latitude = 48.1147,
            longitude = -1.6794,
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5d/Le_Parlement_de_Bretagne_vu_depuis_la_place_-_Rennes.jpg/1280px-Le_Parlement_de_Bretagne_vu_depuis_la_place_-_Rennes.jpg"
        )

        val LISTE_RENNES = listOf(
            DEFAULT,

            Lieu(
                id = "lieu_2",
                nom = "Parc du Thabor",
                description = "Grand jardin public de 10 hectares au cœur de Rennes, mêlant jardin à la française, jardin à l'anglaise et jardin botanique. Un lieu incontournable pour se promener.",
                categorie = CategorieLieu.Parc,
                adresse = "Place Saint-Mélaine, 35000 Rennes",
                latitude = 48.1120,
                longitude = -1.6663,
                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a5/Parc_du_Thabor%2C_%C3%A0_Rennes.jpg/1280px-Parc_du_Thabor%2C_%C3%A0_Rennes.jpg"
            ),

            Lieu(
                id = "lieu_3",
                nom = "Les Halles Centrales",
                description = "Marché couvert emblématique de Rennes, lieu de vie et de gastronomie bretonne. On y trouve produits frais, crêpes, fruits de mer et spécialités locales.",
                categorie = CategorieLieu.Marche,
                adresse = "Place Honoré Commeurec, 35000 Rennes",
                latitude = 48.1108,
                longitude = -1.6811,
                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/99/Halles_centrales_de_Rennes.jpg/1280px-Halles_centrales_de_Rennes.jpg"
            ),

            Lieu(
                id = "lieu_4",
                nom = "Cathédrale Saint-Pierre",
                description = "Cathédrale de style néo-classique, siège de l'archidiocèse de Rennes. Sa façade monumentale et son intérieur richement décoré valent le détour.",
                categorie = CategorieLieu.Monument,
                adresse = "Rue de la Monnaie, 35000 Rennes",
                latitude = 48.1134,
                longitude = -1.6846,
                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fe/Rennes_-_Cath%C3%A9drale_Saint-Pierre_-1.jpg/800px-Rennes_-_Cath%C3%A9drale_Saint-Pierre_-1.jpg"
            ),

            Lieu(
                id = "lieu_5",
                nom = "Musée des Beaux-Arts",
                description = "Musée abritant des collections de peintures, sculptures et objets d'art du XIVe siècle à nos jours. On y trouve des œuvres de Rubens, Véronèse et Picasso.",
                categorie = CategorieLieu.Musee,
                adresse = "20 Quai Émile Zola, 35000 Rennes",
                latitude = 48.1094,
                longitude = -1.6752,
                imageUrl = "https://www.tourisme-rennes.com/voy_content/uploads/2023/09/Musee-beaux-arts-1-1024x682.jpg"
            ),

            Lieu(
                id = "lieu_6",
                nom = "Place des Lices",
                description = "Place historique où se tenaient autrefois des tournois. Aujourd'hui, elle accueille l'un des plus grands marchés de France chaque samedi matin.",
                categorie = CategorieLieu.Marche,
                adresse = "Place des Lices, 35000 Rennes",
                latitude = 48.1142,
                longitude = -1.6838,
                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4c/Rennes_-_H%C3%B4tels_Place_des_Lices.jpg/1280px-Rennes_-_H%C3%B4tels_Place_des_Lices.jpg"
            ),

            Lieu(
                id = "lieu_7",
                nom = "Opéra de Rennes",
                description = "Théâtre à l'italienne construit en 1836, situé sur la place de la Mairie. Il propose opéras, ballets et concerts dans un cadre somptueux.",
                categorie = CategorieLieu.Monument,
                adresse = "Place de la Mairie, 35000 Rennes",
                latitude = 48.1118,
                longitude = -1.6794,
                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/85/Rennes_-_Op%C3%A9ra.jpg/1280px-Rennes_-_Op%C3%A9ra.jpg"
            ),

            Lieu(
                id = "lieu_8",
                nom = "Crêperie La Saint-Georges",
                description = "Crêperie traditionnelle bretonne en plein centre-ville. Galettes de sarrasin et crêpes de froment à déguster dans un cadre chaleureux.",
                categorie = CategorieLieu.Restaurant,
                adresse = "11 Rue du Chapitre, 35000 Rennes",
                latitude = 48.1141,
                longitude = -1.6830,
                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2e/Galette_saucisse_rennes.jpg/1280px-Galette_saucisse_rennes.jpg"
            ),

            Lieu(
                id = "lieu_9",
                nom = "Portes Mordelaises",
                description = "Vestiges médiévaux de l'enceinte fortifiée de Rennes. Ces portes du XVe siècle sont les derniers témoins des remparts de la ville.",
                categorie = CategorieLieu.Monument,
                adresse = "Rue des Portes Mordelaises, 35000 Rennes",
                latitude = 48.1131,
                longitude = -1.6860,
                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/07/Portes_mordelaises_Rennes.jpg/800px-Portes_mordelaises_Rennes.jpg"
            ),

            Lieu(
                id = "lieu_10",
                nom = "Le Coq Gadby",
                description = "Restaurant gastronomique étoilé, l'une des meilleures tables de Rennes. Cuisine créative mettant en valeur les produits bretons de saison.",
                categorie = CategorieLieu.Restaurant,
                adresse = "156 Rue d'Antrain, 35700 Rennes",
                latitude = 48.1229,
                longitude = -1.6770,
                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c2/Rennes_centre.jpg/1280px-Rennes_centre.jpg"
            ),

            Lieu(
                id = "lieu_11",
                nom = "Rue Saint-Michel (Rue de la Soif)",
                description = "Célèbre rue piétonne de Rennes connue pour ses nombreux bars. Ambiance étudiante et festive garantie, surtout le jeudi soir.",
                categorie = CategorieLieu.Bar,
                adresse = "Rue Saint-Michel, 35000 Rennes",
                latitude = 48.1132,
                longitude = -1.6812,
                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/98/Rue_Saint-Michel_Rennes.JPG/800px-Rue_Saint-Michel_Rennes.JPG"
            ),

            Lieu(
                id = "lieu_12",
                nom = "Champs Libres",
                description = "Équipement culturel majeur regroupant le Musée de Bretagne, l'Espace des Sciences et la bibliothèque. Architecture contemporaine remarquable.",
                categorie = CategorieLieu.Musee,
                adresse = "10 Cours des Alliés, 35000 Rennes",
                latitude = 48.1052,
                longitude = -1.6749,
                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/83/Champs_Libres_-_Rennes.jpg/1280px-Champs_Libres_-_Rennes.jpg"
            ),

            Lieu(
                id = "lieu_13",
                nom = "Parc Oberthür",
                description = "Jardin public de 5 hectares classé jardin remarquable, avec des arbres centenaires, un ruisseau et une collection botanique impressionnante.",
                categorie = CategorieLieu.Parc,
                adresse = "Rue Albert 1er, 35000 Rennes",
                latitude = 48.1087,
                longitude = -1.6622,
                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0b/Jardin_Oberthur.jpg/1280px-Jardin_Oberthur.jpg"
            ),

            Lieu(
                id = "lieu_14",
                nom = "Maison de la Brasserie",
                description = "Bar à bières artisanales dans le quartier historique. Large sélection de bières bretonnes et belges dans une ambiance conviviale.",
                categorie = CategorieLieu.Bar,
                adresse = "3 Allée Rallier du Baty, 35000 Rennes",
                latitude = 48.1130,
                longitude = -1.6795,
                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c2/Rennes_centre.jpg/1280px-Rennes_centre.jpg"
            ),

            Lieu(
                id = "lieu_15",
                nom = "Écomusée de la Bintinais",
                description = "Musée consacré à la vie rurale bretonne sur 15 hectares. Ferme pédagogique, expositions sur l'agriculture et les traditions locales.",
                categorie = CategorieLieu.Musee,
                adresse = "Route de Châtillon-sur-Seiche, 35200 Rennes",
                latitude = 48.0830,
                longitude = -1.6674,
                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c2/Rennes_centre.jpg/1280px-Rennes_centre.jpg"
            ),

            Lieu(
                id = "lieu_16",
                nom = "Place de la République",
                description = "Grande place au cœur de Rennes, point de rencontre central. Entourée de bâtiments administratifs et du Palais du Commerce.",
                categorie = CategorieLieu.Monument,
                adresse = "Place de la République, 35000 Rennes",
                latitude = 48.1098,
                longitude = -1.6793,
                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9d/Rennes_Place_de_la_R%C3%A9publique.jpg/1280px-Rennes_Place_de_la_R%C3%A9publique.jpg"
            ),

            Lieu(
                id = "lieu_17",
                nom = "Le Bistrot de la Cité",
                description = "Brasserie traditionnelle face au Parlement. Cuisine du terroir, fruits de mer et ambiance chaleureuse au cœur du centre historique.",
                categorie = CategorieLieu.Restaurant,
                adresse = "1 Rue de la Monnaie, 35000 Rennes",
                latitude = 48.1143,
                longitude = -1.6802,
                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c2/Rennes_centre.jpg/1280px-Rennes_centre.jpg"
            ),

            Lieu(
                id = "lieu_18",
                nom = "Basilique Saint-Sauveur",
                description = "Église du XVIIe siècle de style classique, connue pour sa Vierge miraculeuse. Classée monument historique depuis 1931.",
                categorie = CategorieLieu.Monument,
                adresse = "5 Rue Saint-Sauveur, 35000 Rennes",
                latitude = 48.1126,
                longitude = -1.6827,
                imageUrl = "https://inventaire-des-orgues.fr/media/35/FR-35238-RENNE-STSAUV1-T/images/900px-Orgue_de_la_basilique_saint_Sauveur_Rennes_Ille_nn9S8l8.jpg"
            ),

            Lieu(
                id = "lieu_19",
                nom = "Parc de Gayeulles",
                description = "Plus grand espace vert de Rennes (100 hectares). Parcours sportifs, mini-golf, plan d'eau, aire de jeux. Idéal pour les familles.",
                categorie = CategorieLieu.Parc,
                adresse = "Avenue des Gayeulles, 35700 Rennes",
                latitude = 48.1263,
                longitude = -1.6433,
                imageUrl = "https://images.mnstatic.com/30/6c/306ce51ea2aead7e4cf315c190897b00.jpg"
            ),

            Lieu(
                id = "lieu_20",
                nom = "Hôtel de Ville",
                description = "Mairie de Rennes construite par Jacques Gabriel au XVIIIe siècle. La tour de l'horloge et la niche de la statue de Louis XV sont remarquables.",
                categorie = CategorieLieu.Monument,
                adresse = "Place de la Mairie, 35000 Rennes",
                latitude = 48.1115,
                longitude = -1.6792,
                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/00/Rennes_-_H%C3%B4tel_de_ville.jpg/1280px-Rennes_-_H%C3%B4tel_de_ville.jpg"
            )
        )
    }
}
