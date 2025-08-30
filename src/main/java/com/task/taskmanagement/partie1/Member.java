package com.task.taskmanagement.partie1;

import java.util.List;
import java.util.ArrayList;

//Creation de la classe abstraite Member
public class Member extends User {
    private int score;
    private List<Task> tasks;

    // Constructeur de la classe Member
    public Member(String id, String name, Organisation organisation) {
        super(id, name, organisation);
        this.score = 0;
        this.tasks = new ArrayList<>();
    }

    // Methode pour recuperer le score
    public int getScore() {
        return score;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    // Methode pour recuperer la liste des taches
    public List<Task> getTasks() {
        return tasks;
    }

    // Méthode pour compléter une tâche
    public void completeTask(Task task) {
        if (!tasks.contains(task)) {
            System.out.println("Cette tâche ne vous est pas assignée.");
            return;
        }

        task.setStatus(TaskStatus.DONE);
        this.score += task.calculateTotalPoints();

        // Créer la tâche suivante si c'est une tâche principale
        if (task.getParentTask() == null) {
            TaskSequenceService sequenceService = new TaskSequenceService();
            Task nextTask = sequenceService.createNextTaskInSequence(task, getOrganisation());
            if (nextTask != null) {
                nextTask.assignTo(this);
                this.addTask(nextTask);
                getOrganisation().addTask(nextTask);
                System.out.println("Une nouvelle tâche a été créée: " + nextTask.getDescription());
            }
        }
    }

    // Méthode pour ajouter une sous-tâche
    public void addSubTask(Task parentTask, String description, int estimatedDuration) {
        if (!tasks.contains(parentTask)) {
            System.out.println("La tâche parente ne vous est pas assignée.");
            return;
        }

        String newId = getOrganisation().generateNextTaskId();
        Task subTask = new Task(
                newId,
                description,
                parentTask.getType(),
                parentTask.isProfessional(),
                estimatedDuration,
                TaskStatus.PLANNED);

        parentTask.addSubTask(subTask);
        subTask.assignTo(this);
        this.addTask(subTask);
        getOrganisation().addTask(subTask);
    }

    // Methode ajouter un outil a une tache
    public void addToolToTask(Task task, Tool tool) {
        // Verifier si la tache est assignee au membre
        if (!tasks.contains(task)) {
            System.out.println("Cette tâche ne vous est pas assignée, vous ne pouvez pas ajouter d'outil");
            return;
        }

        // Verifier si l'outil est disponible
        if (!tool.isAvailable()) {
            System.out.println("Cet outil n'est pas disponible");
            return;
        }
        task.addTool(tool);
    }

    // lister les outils d'une tache
    public void listTools(Task task) {
        if (!tasks.contains(task)) {
            System.out.println("Cette tâche ne vous est pas assignée.");
            return;
        }

        System.out.println("\n=== Outils utilisés pour la tâche: " + task.getDescription() + " ===");
        List<Tool> tools = task.getUsedTools();

        if (tools.isEmpty()) {
            System.out.println("Aucun outil n'est utilisé pour cette tâche.");
            return;
        }

        for (Tool tool : tools) {
            System.out.println("ID: " + tool.getId());
            System.out.println("Nom: " + tool.getName());
            System.out.println("Type: " + (tool instanceof ElectricTool ? "Électrique" : "Mécanique"));
            System.out.println("-------------");
        }
    }

    // Methode pour ajouter un commentaire
    public void addCommentToTask(Task task, String comment) {
        if (!tasks.contains(task)) {
            System.out.println("Cette tâche ne vous est pas assignée.");
            return;
        }

        task.addComment(comment);
    }

    // Méthode pour voir le progrès d'une tâche
    public void viewTaskProgress(Task task) {
        if (!tasks.contains(task)) {
            System.out.println("Cette tâche ne vous est pas assignée.");
            return;
        }

        System.out.println("\n=== Progrès de la tâche: " + task.getDescription() + " ===");
        System.out.println("Progrès: " + String.format("%.2f", task.calculateProgress()) + "%");

        if (!task.getSubTasks().isEmpty()) {
            System.out.println("\nSous-tâches:");
            for (Task subTask : task.getSubTasks()) {
                System.out.println("- " + subTask.getDescription() + ": "
                        + String.format("%.2f", subTask.calculateProgress()) + "%");
            }
        }
    }

    // Afficher les informations personnelles du membre
    public void viewPersonalInfo() {
        System.out.println("\n=== Informations utilisatur ===");
        System.out.println("ID: " + getId());
        System.out.println("Nom: " + getName());
        System.out.println("Score: " + score);
        System.out.println("Nombre de tâches: " + tasks.size());
        System.out.println("Type: " + (this instanceof Volunteer ? "Volontaire" : "Employé"));
    }

    // Methode afficher les taches assignees au membre
    public void listTasks() {
        System.out.println("\n=== Mes tâches ===");
        if (tasks.isEmpty()) {
            System.out.println("Vous n'avez pas de tâches assignées.");
            return;
        }

        for (Task task : tasks) {
            System.out.println("ID: " + task.getId());
            System.out.println("Description: " + task.getDescription());
            System.out.println("Type: " + task.getType());
            System.out.println("Statut: " + task.getStatus());
            System.out.println("-------------");
        }
    }
}
