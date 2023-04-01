package com.digdes.school;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        JavaSchoolStarter starter = new JavaSchoolStarter();
        try {
            //Вставка строки в коллекцию
            List<Map<String, Object>> result1 = starter.execute("INSERT VALUES 'lastName' = 'Федоров' , 'id'=3, 'age'=40, 'active'=true");
            List<Map<String, Object>> result2 = starter.execute("INSERT VALUES 'lAstName' = 'Петров' , 'id'=1, 'age'=20, 'cost'=11");
            List<Map<String, Object>> result3 = starter.execute("INSERT VALUES 'LASTName' = 'Сидоров' , 'id'=2, 'cost'=19, 'active'=false");
            //List<Map<String, Object>> delete = starter.execute("UPDATE VALUES 'cost' = 111");
            //Изменение значения которое выше записывали
            List<Map<String, Object>> result4 = starter.execute("UPDATE VALUES 'active'=false, 'cost'=10.1 where 'id'=3");
            List<Map<String, Object>> result5 = starter.execute("UPDATE VALUES 'active'=false, 'cost'=99 where 'age'=20");
            List<Map<String, Object>> result6 = starter.execute("UPDATE VALUES 'age'=10, 'active'=true where 'lastNAME' = 'Сидоров'");
            List<Map<String, Object>> update = starter.execute("UPDATE VALUES 'cost'=999");
            List<Map<String, Object>> special = starter.execute("SELECT WHERE ‘age’>=30 and ‘lastName’ ilike ‘%п%’ '%r' 'p%'");
            //Получение всех данных из коллекции (т.е. в данном примере вернется 1 запись)
            List<Map<String, Object>> result7 = starter.execute("SELECT");
            //List<Map<String, Object>> result9 = starter.execute("SELECT WHERE ‘age’>=30 and ‘lastName’ ilike like or <= != < >");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
