import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");
        List<Employee> list2 = parseXML("data.xml");
        String xmlToJson = listToJson(list2);
        writeString(xmlToJson, "data2.json");
        String json2 = readString("data.json");
        List<Employee> jsonList = jsonToList(json2);
        for (Employee e : jsonList) {
            System.out.println(e.toString());
        }
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (FileReader fileReader = new FileReader(fileName)) {
            CSVReader csvReader = new CSVReader(fileReader);
            ColumnPositionMappingStrategy columnPositionMappingStrategy = new ColumnPositionMappingStrategy();
            columnPositionMappingStrategy.setType(Employee.class);
            columnPositionMappingStrategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(csvReader).
                    withMappingStrategy(columnPositionMappingStrategy).build();
            return csvToBean.parse();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static List<Employee> parseXML(String fileName) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        List<Employee> list = new ArrayList<>();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (Node.ELEMENT_NODE != nodeList.item(i).getNodeType()) {
                    continue;
                }
                Element element = (Element) nodeList.item(i);
                long id = Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent());
                String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                String country = element.getElementsByTagName("country").item(0).getTextContent();
                int age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());
                Employee employee = new Employee(id, firstName, lastName, country, age);
                list.add(employee);
            }
            return list;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(list, listType);
    }

    public static void writeString(String json, String fileName) {
        File file = new File(fileName);
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(json);
            fileWriter.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String readString(String pathName) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(pathName))) {
            JSONParser parser = new JSONParser();
            Object object = parser.parse(bufferedReader);
            JSONArray jsonArray = (JSONArray) object;
            return jsonArray.toJSONString();
        } catch (IOException | ParseException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static List<Employee> jsonToList(String json) {
        List<Employee> list = new ArrayList<>();
        try {
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(json);
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            for (Object o : jsonArray) {
                JSONObject jsonObject = (JSONObject) o;
                Employee employee = gson.fromJson(String.valueOf(jsonObject), Employee.class);
                list.add(employee);
            }
            return list;
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}