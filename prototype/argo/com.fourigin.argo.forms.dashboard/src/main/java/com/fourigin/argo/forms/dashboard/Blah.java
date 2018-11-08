//package com.fourigin.argo.forms.dashboard;
//
//import org.apache.commons.io.IOUtils;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.security.SecureRandom;
//import java.text.SimpleDateFormat;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//import java.util.Random;
//
//public class Blah {
//    public static void main(String[] args) {
//        List<String> userEntries = Arrays.asList(
//            "Tiger Nixon",
//            "Garrett Winters",
//            "Ashton Cox",
//            "Cedric Kelly",
//            "Airi Satou",
//            "Brielle Williamson",
//            "Herrod Chandler",
//            "Rhona Davidson",
//            "Colleen Hurst",
//            "Sonya Frost",
//            "Jena Gaines",
//            "Quinn Flynn",
//            "Charde Marshall",
//            "Haley Kennedy",
//            "Tatyana Fitzpatrick",
//            "Michael Silva",
//            "Paul Byrd",
//            "Gloria Little",
//            "Bradley Greer",
//            "Dai Rios",
//            "Jenette Caldwell",
//            "Yuri Berry",
//            "Caesar Vance",
//            "Doris Wilder",
//            "Angelica Ramos",
//            "Gavin Joyce",
//            "Jennifer Chang",
//            "Brenden Wagner",
//            "Fiona Green",
//            "Shou Itou",
//            "Michelle House",
//            "Suki Burks",
//            "Prescott Bartlett",
//            "Gavin Cortez",
//            "Martena Mccray",
//            "Unity Butler",
//            "Howard Hatfield",
//            "Hope Fuentes",
//            "Vivian Harrell",
//            "Timothy Mooney",
//            "Jackson Bradshaw",
//            "Olivia Liang",
//            "Bruno Nash",
//            "Sakura Yamamoto",
//            "Thor Walton",
//            "Finn Camacho",
//            "Serge Baldwin",
//            "Zenaida Frank",
//            "Zorita Serrano",
//            "Jennifer Acosta",
//            "Cara Stevens",
//            "Hermione Butler",
//            "Lael Greer",
//            "Jonas Alexander",
//            "Shad Decker",
//            "Michael Bruce",
//            "Donna Snider"
//        );
//
//        StringBuilder builder = new StringBuilder();
//        builder.append("{\n");
//        builder.append("\t\"data\": [\n");
//
//        int id = 1;
//        for (String entry : userEntries) {
//            String[] parts = entry.split(" ");
//            String firstName = parts[0];
//            String lastName = parts[1];
//            String email = firstName.toLowerCase(Locale.US) + '.' + lastName.toLowerCase(Locale.US) + "@gmail.com";
//
//            builder.append("\t\t[\n");
//            builder.append("\t\t\t\"").append(id).append("\",\n");
//            builder.append("\t\t\t\"").append(firstName).append("\",\n");
//            builder.append("\t\t\t\"").append(lastName).append("\",\n");
//            builder.append("\t\t\t\"").append(email).append("\"\n");
//            builder.append("\t\t]");
//
//            if(id < userEntries.size()){
//                builder.append(",\n");
//            }
//            else {
//                builder.append("\n");
//            }
//
//            id++;
//        }
//
//        builder.append("\t]\n");
//        builder.append("}\n");
//
//        File usersFile = new File("src/main/web/data/users.json");
//        System.out.println("users-file: " + usersFile.getAbsolutePath());
//
//        try (FileWriter fw = new FileWriter(usersFile)) {
//            IOUtils.write(builder.toString(), fw);
//            fw.flush();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//
//
//        List<Integer> requestEntries = Arrays.asList(
//            1,2,2,1,5,4,7,7,1,4,5,2,12,17,8
//        );
//
//        builder = new StringBuilder();
//        builder.append("{\n");
//        builder.append("\t\"data\": [\n");
//
//        List<String> requestTypes = Arrays.asList(
//            "KFZ-Anmeldung",
//            "KFZ-Ummeldung",
//            "KFZ-Abmeldung"
//        );
//
//        Random random = new SecureRandom();
//
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
//        long now = System.currentTimeMillis();
//
//        id = 1001;
//        for (Integer clientId : requestEntries) {
//            String type = requestTypes.get(random.nextInt(3));
//            Date date = new Date(now - random.nextInt(100)*1000*3600);
//
//            String formattedDate = dateFormat.format(date);
//
//            builder.append("\t\t[\n");
//            builder.append("\t\t\t\"").append(id).append("\",\n");
//            builder.append("\t\t\t\"").append(type).append("\",\n");
//            builder.append("\t\t\t\"").append(clientId).append("\",\n");
//            builder.append("\t\t\t\"").append(formattedDate).append("\"\n");
//            builder.append("\t\t]");
//
//            if((id-1000) < requestEntries.size()){
//                builder.append(",\n");
//            }
//            else {
//                builder.append("\n");
//            }
//
//            id++;
//        }
//
//        builder.append("\t]\n");
//        builder.append("}\n");
//
//        File requestsFile = new File("src/main/web/data/requests.json");
//        System.out.println("requests-file: " + requestsFile.getAbsolutePath());
//
//        try (FileWriter fw = new FileWriter(requestsFile)) {
//            IOUtils.write(builder.toString(), fw);
//            fw.flush();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }
//}
