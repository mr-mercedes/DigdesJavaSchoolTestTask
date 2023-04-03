package com.digdes.school;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        JavaSchoolStarter starter = new JavaSchoolStarter();
        try {
            //Вставка строки в коллекцию
            List<Map<String, Object>> result1 = starter.execute("INSERT VALUES 'lastName' = 'Федоров' , 'id'=3, 'age'=15, 'active'=true");
            List<Map<String, Object>> updateOr = starter.execute("UPDATE VALUES 'age'=10 where 'id'=3 or 'cost'=11");

            List<Map<String, Object>> result2 = starter.execute("INSERT VALUES 'lastName' = 'Иванов' , 'id'=2, 'age'=40, 'cost'=13");
            List<Map<String, Object>> updateOr2 = starter.execute("UPDATE VALUES 'active'=false where 'id'=3 or 'active'=null");

            List<Map<String, Object>> result3 = starter.execute("INSERT VALUES 'lastName' = 'Георгиев' , 'id'=1, 'age'=30, 'cost'=10");
            List<Map<String, Object>> updateAnd = starter.execute("UPDATE VALUES 'active'=false, 'cost'=10.1 where 'cost'<13 and 'age'=30");

            List<Map<String, Object>> delete = starter.execute("DELETE WHERE 'id'=3 or 'cost'=19");
            List<Map<String, Object>> result4 = starter.execute("INSERT VALUES 'lAstName' = 'Петров' , 'id'=3, 'age'=20, 'cost'=11 'active'=true" );
            List<Map<String, Object>> select = starter.execute("SELECT WHERE 'id'=3 and 'active'=true");

            List<Map<String, Object>> updateAll = starter.execute("UPDATE VALUES 'cost' = 111");
            //Изменение значения которое выше записывали

//            List<Map<String, Object>> result5 = starter.execute("UPDATE VALUES 'active'=false, 'cost'=99 where 'age'=20");
//            List<Map<String, Object>> result6 = starter.execute("UPDATE VALUES 'age'=10, 'active'=true where 'lastNAME' = 'Сидоров'");
//            List<Map<String, Object>> update = starter.execute("UPDATE VALUES 'cost'=999");
            //Получение всех данных из коллекции (т.е. в данном примере вернется 1 запись)
            //List<Map<String, Object>> result9 = starter.execute("SELECT WHERE ‘age’>=30 and ‘lastName’ ilike like or <= != < >");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
