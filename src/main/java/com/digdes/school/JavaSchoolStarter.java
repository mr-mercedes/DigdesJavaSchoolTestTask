package com.digdes.school;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JavaSchoolStarter {
    private final List<Map<String, Object>> table = new ArrayList<>();

    private Long id;
    private String lastname;
    private Double cost;
    private Long age;
    private Boolean active;

    public JavaSchoolStarter() {
    }


    public List<Map<String, Object>> execute(String request) throws Exception {
        List<String> tokens = getTokens(request);
        String operation = tokens.get(0);
        switch (operation) {
            case "INSERT" -> insert(tokens);
            case "UPDATE" -> update(tokens);
            case "DELETE" -> delete(tokens);
            case "SELECT" -> {
                return select(tokens);
            }
            default -> throw new RuntimeException("Unknown operation " + operation);
        }
        return table;
    }

    private void insert(List<String> tokens) {
        if (!tokens.contains("VALUES"))
            throw new RuntimeException("Error SQL syntax: INSERT operation expected VALUES");
        createRow(tokens);
        Map<String, Object> newRow = fillRow();
        table.add(newRow);
    }

    private void createRow(List<String> tokens) {
        clearOldData();
        for (int i = 2; i < tokens.size(); i++) {
            String item = tokens.get(i);
            switch (item.toLowerCase()) {
                case "lastname" -> lastname = tokens.get(i + 2);
                case "id" -> id = Long.parseLong(tokens.get(i + 2));
                case "age" -> age = Long.parseLong(tokens.get(i + 2));
                case "cost" -> this.cost = Double.parseDouble(tokens.get(i + 2));
                case "active" -> this.active = Boolean.valueOf(tokens.get(i + 2));
            }
        }
    }

    private Map<String, Object> fillRow() {
        Map<String, Object> newRow = new HashMap<>();
        newRow.put("id", id);
        newRow.put("lastName", lastname);
        newRow.put("age", age);
        newRow.put("cost", cost);
        newRow.put("active", active);
        return newRow;
    }

    private void update(List<String> tokens) {
        if (!tokens.contains("VALUES"))
            throw new RuntimeException("Error SQL syntax: UPDATE operation expected VALUES");
        if (!tokens.contains("WHERE")) {
            updateWithoutWhere(tokens);
        } else {
            updateWithWhere(tokens);
        }
    }

    private void updateWithoutWhere(List<String> tokens) {
        int index = 0;
        while (table.size() > index) {
            Map<String, Object> row = table.get(index);
            updateRow(tokens, row);
            Map<String, Object> updateRow = fillRow();
            table.remove(index);
            table.add(index, updateRow);
            index++;
        }
    }

    private void updateWithWhere(List<String> tokens) {
        int index = 0;
        Map<String, Object> updateKey = extractActionKey(tokens);
        while (table.size() > index) {
            Map<String, Object> row = table.get(index);
            for (Map.Entry<String, Object> map : updateKey.entrySet()) {
                String key = map.getKey();
                Object value = map.getValue();
                if (row.containsKey(key) && Objects.equals(row.get(key), value)) {
                    updateRow(tokens, row);
                    Map<String, Object> updateRow = fillRow();
                    table.remove(index);
                    table.add(index, updateRow);
                }
            }
            index++;
        }
    }

    private Map<String, Object> extractActionKey(List<String> tokens) {
        String actionKey = null;
        Object actionValue = null;
//        String actionOperator = null;
        for (int i = 2; i < tokens.size(); i++) {
            String token = tokens.get(i);
//            actionOperator = tokens.get(i + 2);
//            Boolean compareOperator = compare(actionOperator);
            if (token.equalsIgnoreCase("WHERE")) {
                actionKey = tokens.get(i + 1);
                switch (actionKey.toLowerCase()) {
                    case "lastname" -> actionValue = tokens.get(i + 3);
                    case "id", "age" -> actionValue = Long.parseLong(tokens.get(i + 3));
                    case "cost" -> actionValue = Double.parseDouble(tokens.get(i + 3));
                    case "active" -> actionValue = Boolean.valueOf(tokens.get(i + 3));
                }
            }
        }

        assert actionKey != null;
        assert actionValue != null;
        return Map.of(actionKey, actionValue);
    }

//    private Boolean compare(String actionOperator) {
//        switch (actionOperator){
//            case "<=", ">=", "!=", "<", ">" -> {
//
//            }
//            case ">="
//        }
//        return null;
//    }

    private void updateRow(List<String> tokens, Map<String, Object> row) {
        clearOldData();
        extractOldCol(row);
        updateCol(tokens);
    }

    private void extractOldCol(Map<String, Object> row) {
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            switch (key.toLowerCase()) {
                case "lastname" -> lastname = (String) value;
                case "id" -> id = (Long) value;
                case "age" -> age = (Long) value;
                case "cost" -> cost = (Double) value;
                case "active" -> active = (Boolean) value;
            }
        }
    }

    private void updateCol(List<String> tokens) {
        for (int i = 2; i < tokens.size(); i++) {
            String item = tokens.get(i);
            switch (item.toLowerCase()) {
                case "lastname" -> lastname = tokens.get(i + 2);
                case "id" -> id = Long.parseLong(tokens.get(i + 2));
                case "age" -> age = Long.parseLong(tokens.get(i + 2));
                case "cost" -> cost = Double.parseDouble(tokens.get(i + 2));
                case "active" -> active = Boolean.valueOf(tokens.get(i + 2));
            }
        }
    }

    private void delete(List<String> tokens) {
        if (!tokens.contains("WHERE")) {
            deleteWithoutWhere();
        } else {
            deleteWithWhere(tokens);
        }
    }

    private void deleteWithWhere(List<String> tokens) {
        Map<String, Object> actionKey = extractActionKey(tokens);
        for (Map<String, Object> row : table) {
            for (Map.Entry<String, Object> actions : actionKey.entrySet()) {
                String key = actions.getKey();
                Object value = actions.getValue();
                if (row.containsKey(key) && Objects.equals(row.get(key), value)) {
                    table.remove(row);
                }
            }
        }
    }

    private void deleteWithoutWhere() {
        table.clear();
    }

    private List<Map<String, Object>> select(List<String> tokens) {
        List<Map<String, Object>> table;
        if (!tokens.contains("WHERE")) {
            table = selectWithoutWhere();
        } else {
            table = selectWithWhere(tokens);
        }
        return table;
    }

    private List<Map<String, Object>> selectWithWhere(List<String> tokens) {
        Map<String, Object> actionKey = extractActionKey(tokens);
        List<Map<String, Object>> selectTable = new ArrayList<>();
        for (Map.Entry<String, Object> keys : actionKey.entrySet()) {
            String key = keys.getKey();
            Object value = keys.getValue();
            for (Map<String, Object> row : table) {
                if (row.containsKey(key) && Objects.equals(row.get(key), value)) { //ошибка стравнения двух строк с разным регистром
                    selectTable.add(row);
                }
            }
        }
        return selectTable;
    }

    private List<Map<String, Object>> selectWithoutWhere() {
        return table;
    }

    private List<String> getTokens(String request) {
        checkColumnName(request);
        String regex = "([A-Za-z_.0-9]+)|([А-Яа-я_.0-9]+)|([0-9])|(=|!=|<=|>=|>|<)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(request);
        List<String> tokens = new ArrayList<>();

        while (matcher.find()) {
            tokens.add(convertOperators(matcher.group()));
        }
        return tokens;
    }

    private String convertOperators(String token) {
        switch (token.toUpperCase()) {
            case "INSERT", "UPDATE", "DELETE", "SELECT", "VALUE", "WHERE", "LIKE", "ILIKE", "OR", "AND" -> {
                return token.toUpperCase();
            }
            default -> {
                return token;
            }
        }
    }

    private void clearOldData() {
        id = null;
        lastname = null;
        age = null;
        cost = null;
        active = null;
    }

    private void checkColumnName(String request) {
        List<String> trueColumnNames = Arrays.asList("id", "cost", "age", "active", "lastname");
        List<String> tokensColumnNames = extractColumnNames(request);
        for (String token : tokensColumnNames) {
            String tokenColumnName = token.substring(1, token.length() - 1);
            if (!trueColumnNames.contains(tokenColumnName.toLowerCase())) {
                throw new RuntimeException("Error SQL syntax: unknown column name: " + tokenColumnName);
            }
        }
    }

    private List<String> extractColumnNames(String request) {
        String regex = "('[A-Za-z']+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(request);

        List<String> tokensColumnNames = new ArrayList<>();
        while (matcher.find()) {
            tokensColumnNames.add(matcher.group());
        }
        return tokensColumnNames;
    }
    String toExtractlike = "(%[A-Za-zА-Яа-я]*|[A-Za-zА-Яа-я]*%)|(%[A-Za-zА-Яа-я]*%)";
    }
}
