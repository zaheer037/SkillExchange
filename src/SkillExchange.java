import java.io.*;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class SkillExchange {
    private static final Map<String, String> userCredentials = new HashMap<>();
    private static final Map<String, List<String>> skillsOffered = new HashMap<>();
    private static final Map<String, List<String>> skillsNeeded = new HashMap<>();
    private static final Map<String, String> userEmails = new HashMap<>();
    private static final Map<String, String> userPhones = new HashMap<>();
    private static final Map<String, List<String>> notifications = new HashMap<>();
    private static final Map<String, List<String>> chatMessages = new HashMap<>();
    private static final Scanner sc = new Scanner(System.in);
    private static String currentUser = null;

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    public static void main(String[] args) {
        loadUserCredentials();
        loadSkillsOffered();
        loadSkillsNeeded();
        loadEmails();
        loadPhones();
        loadNotifications();
        loadChatMessages();

        System.out.println("🎓 Welcome to the Peer Skill Exchange System 🎓");
        while (true) {
            System.out.println("\nMAIN MENU");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice (1-3): ");
            String choice = sc.nextLine();
            switch (choice) {
                case "1":
                    register();
                    break;
                case "2":
                    login();
                    break;
                case "3":
                    saveUserCredentials();
                    saveSkillsOffered();
                    saveSkillsNeeded();
                    saveEmails();
                    savePhones();
                    saveNotifications();
                    saveChatMessages();
                    System.out.println("✅ Data saved. Thank you for using Peer Skill Exchange. Goodbye!");
                    return;
                default:
                    System.out.println("❌ Invalid choice. Please enter 1, 2, or 3.");
            }
        }
    }

    private static void register() {
        System.out.print("👤 Enter a new username: ");
        String username = sc.nextLine();
        if (userCredentials.containsKey(username)) {
            System.out.println("⚠️ Username already exists. Please try a different one.");
            return;
        }

        String password;
        while (true) {
            System.out.print("🔑 Set a password (min 8 chars, 1 uppercase, 1 special char): ");
            password = sc.nextLine();
            if (isPasswordValid(password)) {
                break;
            } else {
                System.out.println("❌ Password must be at least 8 characters long with at least one uppercase letter and one special character.");
            }
        }

        String hashedPassword = hashPassword(password);
        String email, phone;
        while (true) {
            System.out.print("📧 Enter your email (must end with @gmail.com): ");
            email = sc.nextLine();
            if (email.endsWith("@gmail.com")) {
                break;
            } else {
                System.out.println("❌ Invalid email. It must end with @gmail.com.");
            }
        }
        while (true) {
            System.out.print("☎️ Enter your phone number (10 digits): ");
            phone = sc.nextLine();
            if (phone.matches("\\d{10}")) {
                break;
            } else {
                System.out.println("❌ Invalid phone number. It must be exactly 10 digits.");
            }
        }
        userCredentials.put(username, hashedPassword);
        userEmails.put(username, email);
        userPhones.put(username, phone);
        skillsOffered.put(username, new ArrayList<>());
        skillsNeeded.put(username, new ArrayList<>());
        notifications.put(username, new ArrayList<>());
        saveUserCredentials();
        saveSkillsOffered();
        saveSkillsNeeded();
        saveEmails();
        savePhones();
        saveNotifications();
        System.out.println("✅ Registration successful! You can now login.");
    }

    private static boolean isPasswordValid(String password) {
        if (password.length() < 8) return false;
        boolean hasUpper = false;
        boolean hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }
        return hasUpper && hasSpecial;
    }

    private static void login() {
        System.out.print("👤 Enter your username: ");
        String username = sc.nextLine();
        System.out.print("🔑 Enter your password: ");
        String password = sc.nextLine();
        String hashedPassword = hashPassword(password);
        if (userCredentials.containsKey(username) && userCredentials.get(username).equals(hashedPassword)) {
            currentUser = username;
            skillsOffered.putIfAbsent(username, new ArrayList<>());
            skillsNeeded.putIfAbsent(username, new ArrayList<>());
            notifications.putIfAbsent(username, new ArrayList<>());
            System.out.println("✅ Login successful! Welcome, " + username + ".");
            userDashboard();
        } else {
            System.out.println("❌ Invalid credentials. Try again.");
        }
    }

    private static void userDashboard() {
        while (true) {
            System.out.println("\n📋 USER DASHBOARD");
            System.out.println("1. Add skills I can teach");
            System.out.println("2. Add skills I want to learn");
            System.out.println("3. View my skills");
            System.out.println("4. Find skill matches");
            System.out.println("5. View / Edit my profile");
            System.out.println("6. View notifications");
            System.out.println("7. Chat with connections");
            System.out.println("8. Logout");
            System.out.print("Enter your choice (1-8): ");
            String choice = sc.nextLine();
            switch (choice) {
                case "1":
                    addSkill(skillsOffered, "teach");
                    saveSkillsOffered();
                    break;
                case "2":
                    addSkill(skillsNeeded, "learn");
                    saveSkillsNeeded();
                    break;
                case "3":
                    viewMySkills();
                    break;
                case "4":
                    findMatches();
                    break;
                case "5":
                    viewMyProfile();
                    break;
                case "6":
                    viewNotifications();
                    break;
                case "7":
                    viewChat();
                    break;
                case "8":
                    System.out.println("👋 Logged out successfully.");
                    currentUser = null;
                    return;
                default:
                    System.out.println("❌ Invalid choice. Please enter 1 to 8.");
            }
        }
    }

    private static void addSkill(Map<String, List<String>> skillMap, String type) {
        System.out.print("📝 Enter a skill you can " + type + ": ");
        String skill = sc.nextLine().toLowerCase();
        List<String> skills = skillMap.get(currentUser);

        Map<String, List<String>> otherMap = type.equals("teach") ? skillsNeeded : skillsOffered;
        List<String> otherSkills = otherMap.get(currentUser);

        if (skills.contains(skill)) {
            System.out.println("⚠️ You already added \"" + skill + "\" in your " + (type.equals("teach") ? "teaching" : "learning") + " list.");
        } else if (otherSkills != null && otherSkills.stream().anyMatch(s -> s.equalsIgnoreCase(skill))) {
            System.out.println("❌ You cannot add the same skill to both offered and needed lists.");
        } else {
            skills.add(skill);
            System.out.println("✅ Skill \"" + skill + "\" added successfully!");
        }
    }

    private static void viewMySkills() {
        System.out.println("\n🧑 Your Profile:");
        System.out.println("Skills you can teach: " + skillsOffered.get(currentUser));
        System.out.println("Skills you want to learn: " + skillsNeeded.get(currentUser));
    }

    private static void viewMyProfile() {
        System.out.println("\n📇 Your Profile Details:");
        System.out.println("Username: " + currentUser);
        System.out.println("Email: " + userEmails.getOrDefault(currentUser, "N/A"));
        System.out.println("Phone: " + userPhones.getOrDefault(currentUser, "N/A"));
        System.out.println("Skills you can teach: " + skillsOffered.getOrDefault(currentUser, new ArrayList<>()));
        System.out.println("Skills you want to learn: " + skillsNeeded.getOrDefault(currentUser, new ArrayList<>()));
        System.out.print("\n✏️ Do you want to update your email or phone number? (yes/no): ");
        String response = sc.nextLine().toLowerCase();
        if (response.equals("yes") || response.equals("y")) {
            System.out.print("📧 Enter new email (or press Enter to keep current): ");
            String newEmail = sc.nextLine();
            if (!newEmail.trim().isEmpty()) {
                if (newEmail.endsWith("@gmail.com")) {
                    userEmails.put(currentUser, newEmail);
                } else {
                    System.out.println("❌ Email not updated. It must end with @gmail.com.");
                }
            }
            System.out.print("☎️ Enter new phone number (or press Enter to keep current): ");
            String newPhone = sc.nextLine();
            if (!newPhone.trim().isEmpty()) {
                if (newPhone.matches("\\d{10}")) {
                    userPhones.put(currentUser, newPhone);
                } else {
                    System.out.println("❌ Phone number not updated. It must be exactly 10 digits.");
                }
            }
            saveEmails();
            savePhones();
            System.out.println("✅ Contact details updated successfully.");
        } else {
            System.out.println("ℹ️ No changes made.");
        }
    }

    private static void findMatches() {
        List<String> myNeeds = skillsNeeded.get(currentUser);
        if (myNeeds == null || myNeeds.isEmpty()) {
            System.out.println("⚠️ You have not added any skills you want to learn yet.");
            System.out.println("➡️ Please add at least one skill before finding matches.");
            return;
        }
        System.out.println("\n🔍 Searching for matches...");
        Map<Integer, String> matchIndex = new HashMap<>();
        int index = 1;
        for (String otherUser : skillsOffered.keySet()) {
            if (!otherUser.equals(currentUser)) {
                List<String> theirSkills = skillsOffered.get(otherUser);
                for (String need : myNeeds) {
                    if (theirSkills.contains(need)) {
                        System.out.println(index + ". " + otherUser + " can teach you: " + need);
                        matchIndex.put(index, otherUser);
                        index++;
                        break;
                    }
                }
            }
        }
        if (matchIndex.isEmpty()) {
            System.out.println("❌ No matches found right now. Try again later.");
            return;
        }

        while (true) {
            System.out.print("🔗 Enter the number of the user you want to connect with (or 0 to cancel): ");
            try {
                int choice = Integer.parseInt(sc.nextLine());
                if (choice == 0) {
                    return;
                } else if (matchIndex.containsKey(choice)) {
                    String selectedUser = matchIndex.get(choice);
                    String notification = currentUser + " wants to connect with you for skill exchange.";
                    notifications.computeIfAbsent(selectedUser, k -> new ArrayList<>()).add(notification);
                    saveNotifications();
                    System.out.println("📨 Request sent to " + selectedUser + ". They will need to approve before sharing contact details.");
                    return;
                } else {
                    System.out.println("❌ Invalid selection. Please enter a number between 1 and " + (index-1) + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number.");
            }
        }
    }

    private static void viewNotifications() {
        List<String> userNotifications = notifications.getOrDefault(currentUser, new ArrayList<>());
        if (userNotifications.isEmpty()) {
            System.out.println("📭 You have no new notifications.");
            return;
        }

        System.out.println("\n📬 Your Notifications:");
        for (int i = 0; i < userNotifications.size(); i++) {
            System.out.println((i+1) + ". " + userNotifications.get(i));
        }

        // Keep asking until we get a valid yes/no response
        String response;
        while (true) {
            System.out.print("\nDo you want to respond to any notifications? (yes/no): ");
            response = sc.nextLine().toLowerCase();
            if (response.equals("yes") || response.equals("y") ||
                    response.equals("no") || response.equals("n")) {
                break;
            }
            System.out.println("❌ Invalid input. Please enter 'yes' or 'no'.");
        }

        if (response.equals("yes") || response.equals("y")) {
            int choice = -1;
            while (true) {
                System.out.print("Enter the number of the notification you want to respond to: ");
                try {
                    choice = Integer.parseInt(sc.nextLine());
                    if (choice > 0 && choice <= userNotifications.size()) {
                        break;
                    } else {
                        System.out.println("❌ Invalid notification number. Please enter a number between 1 and " + userNotifications.size() + ".");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("❌ Please enter a valid number.");
                }
            }

            String notification = userNotifications.get(choice-1);
            if (notification.contains("wants to connect with you")) {
                String requester = notification.split(" ")[0];
                // Keep asking until we get a valid yes/no response
                String share;
                while (true) {
                    System.out.print("Do you want to share your contact details and open chat with " + requester + "? (yes/no): ");
                    share = sc.nextLine().toLowerCase();
                    if (share.equals("yes") || share.equals("y") ||
                            share.equals("no") || share.equals("n")) {
                        break;
                    }
                    System.out.println("❌ Invalid input. Please enter 'yes' or 'no'.");
                }

                if (share.equals("yes") || share.equals("y")) {
                    String details = currentUser + " has shared their contact details with you:\n" +
                            "Email: " + userEmails.get(currentUser) + "\n" +
                            "Phone: " + userPhones.get(currentUser);
                    notifications.computeIfAbsent(requester, k -> new ArrayList<>()).add(details);

                    // Establish chat connection
                    String chatKey = currentUser.compareTo(requester) < 0 ?
                            currentUser + "_" + requester : requester + "_" + currentUser;
                    chatMessages.putIfAbsent(chatKey, new ArrayList<>());
                    saveChatMessages();

                    System.out.println("✅ Contact details shared with " + requester + ".");
                    System.out.println("You can now chat with " + requester + " from the chat menu.");
                } else {
                    System.out.println("❌ Request declined.");
                }
                userNotifications.remove(choice-1);
                saveNotifications();
            }
        }
    }
    private static void viewChat() {
        // Get all users you've connected with
        List<String> connectedUsers = new ArrayList<>();
        for (String user : chatMessages.keySet()) {
            if (user.startsWith(currentUser + "_") || user.endsWith("_" + currentUser)) {
                String otherUser = user.replace(currentUser + "_", "").replace("_" + currentUser, "");
                connectedUsers.add(otherUser);
            }
        }

        if (connectedUsers.isEmpty()) {
            System.out.println("You have no active chat connections yet.");
            return;
        }

        System.out.println("\n💬 Your chat connections:");
        for (int i = 0; i < connectedUsers.size(); i++) {
            System.out.println((i+1) + ". " + connectedUsers.get(i));
        }

        System.out.print("Select a user to chat with (or 0 to cancel): ");
        try {
            int choice = Integer.parseInt(sc.nextLine());
            if (choice > 0 && choice <= connectedUsers.size()) {
                String selectedUser = connectedUsers.get(choice-1);
                startChat(selectedUser);
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid number.");
        }
    }

    private static void startChat(String otherUser) {
        String chatKey = currentUser.compareTo(otherUser) < 0 ?
                currentUser + "_" + otherUser : otherUser + "_" + currentUser;

        List<String> messages = chatMessages.getOrDefault(chatKey, new ArrayList<>());

        System.out.println("\n💬 Chat with " + otherUser + " (type 'exit' to end)");
        for (String msg : messages) {
            String[] parts = msg.split(":", 2);
            System.out.println(parts[0] + ": " + parts[1]);
        }

        while (true) {
            System.out.print("You: ");
            String message = sc.nextLine();
            if (message.equalsIgnoreCase("exit")) {
                break;
            }
            messages.add(currentUser + ":" + message);
            chatMessages.put(chatKey, messages);
            saveChatMessages();
        }
    }

    private static void loadChatMessages() {
        try (BufferedReader br = new BufferedReader(new FileReader("chats.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    String[] messages = parts[1].split(";;");
                    chatMessages.put(parts[0], new ArrayList<>(Arrays.asList(messages)));
                }
            }
        } catch (IOException e) {
            System.out.println("⚠️ No existing chat data found.");
        }
    }

    private static void saveChatMessages() {
        try (PrintWriter writer = new PrintWriter("chats.txt")) {
            for (Map.Entry<String, List<String>> entry : chatMessages.entrySet()) {
                writer.println(entry.getKey() + ":" + String.join(";;", entry.getValue()));
            }
        } catch (IOException e) {
            System.out.println("❌ Failed to save chat messages.");
        }
    }

    private static void loadNotifications() {
        try (BufferedReader br = new BufferedReader(new FileReader("notifications.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    String[] notifs = parts[1].split(";;");
                    notifications.put(parts[0], new ArrayList<>(Arrays.asList(notifs)));
                }
            }
        } catch (IOException e) {
            System.out.println("⚠️ No existing notifications data found.");
        }
    }

    private static void saveNotifications() {
        try (PrintWriter writer = new PrintWriter("notifications.txt")) {
            for (Map.Entry<String, List<String>> entry : notifications.entrySet()) {
                writer.println(entry.getKey() + ":" + String.join(";;", entry.getValue()));
            }
        } catch (IOException e) {
            System.out.println("❌ Failed to save notifications.");
        }
    }

    private static void loadUserCredentials() {
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    userCredentials.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            System.out.println("⚠️ No existing user data found.");
        }
    }

    private static void saveUserCredentials() {
        try (PrintWriter writer = new PrintWriter("users.txt")) {
            for (Map.Entry<String, String> entry : userCredentials.entrySet()) {
                writer.println(entry.getKey() + ":" + entry.getValue());
            }
        } catch (IOException e) {
            System.out.println("❌ Failed to save user data.");
        }
    }

    private static void loadSkillsOffered() {
        try (BufferedReader br = new BufferedReader(new FileReader("skills_offered.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    List<String> skills = Arrays.asList(parts[1].split(","));
                    skillsOffered.put(parts[0], new ArrayList<>(skills));
                }
            }
        } catch (IOException e) {
            System.out.println("⚠️ No existing skills offered data found.");
        }
    }

    private static void saveSkillsOffered() {
        try (PrintWriter writer = new PrintWriter("skills_offered.txt")) {
            for (Map.Entry<String, List<String>> entry : skillsOffered.entrySet()) {
                writer.println(entry.getKey() + ":" + String.join(",", entry.getValue()));
            }
        } catch (IOException e) {
            System.out.println("❌ Failed to save offered skills.");
        }
    }

    private static void loadSkillsNeeded() {
        try (BufferedReader br = new BufferedReader(new FileReader("skills_needed.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    List<String> skills = Arrays.asList(parts[1].split(","));
                    skillsNeeded.put(parts[0], new ArrayList<>(skills));
                }
            }
        } catch (IOException e) {
            System.out.println("⚠️ No existing skills needed data found.");
        }
    }

    private static void saveSkillsNeeded() {
        try (PrintWriter writer = new PrintWriter("skills_needed.txt")) {
            for (Map.Entry<String, List<String>> entry : skillsNeeded.entrySet()) {
                writer.println(entry.getKey() + ":" + String.join(",", entry.getValue()));
            }
        } catch (IOException e) {
            System.out.println("❌ Failed to save needed skills.");
        }
    }

    private static void loadEmails() {
        try (BufferedReader br = new BufferedReader(new FileReader("emails.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) userEmails.put(parts[0], parts[1]);
            }
        } catch (IOException e) {
            System.out.println("⚠️ No existing email data found.");
        }
    }

    private static void saveEmails() {
        try (PrintWriter writer = new PrintWriter("emails.txt")) {
            for (Map.Entry<String, String> entry : userEmails.entrySet()) {
                writer.println(entry.getKey() + ":" + entry.getValue());
            }
        } catch (IOException e) {
            System.out.println("❌ Failed to save email data.");
        }
    }

    private static void loadPhones() {
        try (BufferedReader br = new BufferedReader(new FileReader("phones.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) userPhones.put(parts[0], parts[1]);
            }
        } catch (IOException e) {
            System.out.println("⚠️ No existing phone data found.");
        }
    }

    private static void savePhones() {
        try (PrintWriter writer = new PrintWriter("phones.txt")) {
            for (Map.Entry<String, String> entry : userPhones.entrySet()) {
                writer.println(entry.getKey() + ":" + entry.getValue());
            }
        } catch (IOException e) {
            System.out.println("❌ Failed to save phone data.");
        }
    }
}