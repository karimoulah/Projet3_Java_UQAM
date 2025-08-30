# Application de Gestion des Tâches

## Auteurs

* **Étudiant 1** : Abdoul Karime DIOP / **Courriel:** [diop.abdoul_karime@courrier.uqam.ca](mailto:diop.abdoul_karime@courrier.uqam.ca)/ ** **Code Permenant** :**DIOA14279808
* **Étudiant 2** : Abass SARR / **Courriel:** [sarr.abass@courrier.uqam.ca](mailto:sarr.abass@courrier.uqam.ca)/ ** **Code Permenant** :**SARA07349709

Ceci est un système complet de gestion des tâches qui permet aux organisations de gérer efficacement les tâches, les membres et les outils. Le système comprend des fonctionnalités d'automatisation des tâches, de gestion des sous-tâches et de suivi des outils.

## Fonctionnalités

### 1. Gestion des Tâches

- Créer et gérer des tâches avec différents types et catégories
- Suivre la progression et le statut des tâches
- Calculer les scores des tâches et la performance des membres
- **Automatisation des Tâches** : Création automatique des tâches suivantes dans la séquence lorsqu'une tâche est terminée
- **Gestion des Sous-tâches** : Créer et gérer des structures hiérarchiques de tâches

### 2. Gestion des Membres

- Différents rôles d'utilisateurs (Admin, Membre, Employé, Bénévole)
- Suivre les scores et la performance des membres
- Assigner des tâches aux membres
- Regroupement des membres par organisation

### 3. Gestion des Outils

- Suivre la disponibilité et l'utilisation des outils
- Assigner des outils aux tâches
- Différents types d'outils (Électrique, Mécanique)
- Gestion des outils par organisation

### 4. Gestion des Organisations

- Créer et gérer des organisations
- Suivre les métriques des organisations (nombre de membres, score total, tâches terminées)
- Gérer les outils et membres spécifiques à l'organisation

### 5. Fonctionnalités d'Automatisation

- Création automatique de séquences de tâches
- Suivi de la progression des tâches complexes
- Calcul des scores pour l'achèvement des tâches
- Mises à jour de statut et notifications

### 6. Fonctionnalités des Sous-tâches

- Créer des structures hiérarchiques de tâches
- Suivre la progression des sous-tâches
- Hériter de l'organisation et de la catégorie des tâches parentes
- Gérer les affectations des sous-tâches

## Points de Terminaison API

### Points de Terminaison des Tâches

- `POST /api/tasks` - Créer une nouvelle tâche (Admin, Member)
  - Admin : peut créer des tâches pour n'importe quel membre de son organisation
  - Member : ne peut créer que des tâches pour lui-même
- `GET /api/tasks/{id}` - Obtenir les détails d'une tâche (Admin, Member)
  - Admin : peut voir toutes les tâches de son organisation
  - Member : ne peut voir que ses propres tâches
- `PUT /api/tasks/{id}/update-status` - Mettre à jour le statut d'une tâche (Admin, Member)
  - Admin : peut mettre à jour n'importe quelle tâche de son organisation
  - Member : ne peut mettre à jour que ses propres tâches
- `POST /api/tasks/{id}/subtasks` - Ajouter une sous-tâche (Admin, Member)
  - Admin : peut ajouter des sous-tâches à n'importe quelle tâche de son organisation
  - Member : ne peut ajouter des sous-tâches qu'à ses propres tâches
- `GET /api/tasks/{id}/subtasks` - Obtenir les sous-tâches (Admin, Member)
  - Admin : peut voir toutes les sous-tâches de son organisation
  - Member : ne peut voir que ses propres sous-tâches
- `GET /api/tasks/{id}/list-tools` - Obtenir les outils utilisés dans une tâche (Member)
  - Member : ne peut voir que les outils des tâches qui lui sont assignées
- `POST /api/tasks/{id}/add-tool` - Ajouter un outil à une tâche (Member)
  - Member : ne peut ajouter des outils qu'à ses propres tâches
- `PUT /api/tasks/{id}/add-comment` - Ajouter un commentaire à une tâche (Member)
  - Member : ne peut ajouter des commentaires qu'à ses propres tâches

### Points de Terminaison des Membres

- `GET /api/members/{id}` - Obtenir les détails d'un membre (Admin)
  - Admin : ne peut voir que les membres de son organisation
- `GET /api/members/organisations/{id}` - Obtenir les membres par organisation (Admin)
  - Admin : ne peut voir que les membres de son organisation

### Points de Terminaison des Organisations

- `POST /api/organisations` - Créer une organisation (Admin)
  - Admin : peut créer une nouvelle organisation
- `GET /api/organisations/{id}` - Obtenir les détails d'une organisation (Admin, Member)
  - Admin : peut voir les détails de son organisation
  - Member : ne peut voir que les détails de son organisation
- `GET /api/organisations/{id}/members` - Obtenir les membres d'une organisation (Admin)
  - Admin : ne peut voir que les membres de son organisation

### Points de Terminaison des Outils

- `GET /api/tools/{id}` - Obtenir les détails d'un outil (Admin, Member)
  - Admin : peut voir tous les outils de son organisation
  - Member : ne peut voir que les outils de son organisation
- `GET /api/tools/organisations/{id}/available` - Obtenir les outils disponibles (Admin, Member)
  - Admin : peut voir tous les outils disponibles de son organisation
  - Member : ne peut voir que les outils disponibles de son organisation

## Sécurité

- Authentification basée sur JWT
- Contrôle d'accès basé sur les rôles
- Sécurité au niveau de l'organisation

## Technologies

- Spring Boot
- Spring Security
- MongoDB
- JWT
- Lombok
- Maven

## Pour Commencer

1. Cloner le dépôt
2. Configurer la connexion MongoDB
3. Exécuter l'application
4. Utiliser les points de terminaison API pour gérer les tâches, les membres et les organisations
