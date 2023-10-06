import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.Scanner;

public class RegistroVentas {
    public static void main(String[] args) {
        try{
            DocumentBuilderFactory DBFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = DBFactory.newDocumentBuilder();
            Document doc = builder.parse(new File("sales.xml"));

            Scanner scanner = new Scanner(System.in);
            double percentageIncrease = obtenerPorcentaje(scanner);

            System.out.print("Que departamento desea modificar? ");
            String deparment = scanner.nextLine();

            Element root = doc.getDocumentElement();

            NodeList  saleRecords = root.getElementsByTagName("sale_record");

            Document newDoc=builder.newDocument();
            Element rootElement = newDoc.createElement("sale_record");
            newDoc.appendChild(rootElement);

            for (int i = 0; i < saleRecords.getLength(); i++) {
                Node saleRecord = saleRecords.item(i);
                if (saleRecord.getNodeType() == Node.ELEMENT_NODE) {
                    Element saleElement = (Element) saleRecord;

                    String departamentActual = saleElement.getElementsByTagName("department").item(0).getTextContent();

                    if (departamentActual.equalsIgnoreCase(deparment)) {

                        double ventasAntiguas = Double.parseDouble(saleElement.getElementsByTagName("sales").item(0).getTextContent());
                        double incremento = (ventasAntiguas * percentageIncrease) / 100;
                        double ventasNuevas = ventasAntiguas + incremento;

                        DecimalFormat df = new DecimalFormat("#.##");
                        String newSalesFormateadas = df.format(ventasNuevas);

                        Element nuevoSaleRecord = newDoc.createElement("sale_record");

                        Element idElement = newDoc.createElement("id");
                        idElement.appendChild(newDoc.createTextNode(saleElement.getElementsByTagName("id").item(0).getTextContent()));
                        Element firstNameElement = newDoc.createElement("first_name");
                        firstNameElement.appendChild(newDoc.createTextNode(saleElement.getElementsByTagName("first_name").item(0).getTextContent()));
                        Element lastNameElement = newDoc.createElement("last_name");
                        lastNameElement.appendChild(newDoc.createTextNode(saleElement.getElementsByTagName("last_name").item(0).getTextContent()));
                        Element salesElement = newDoc.createElement("sales");
                        salesElement.appendChild(newDoc.createTextNode(newSalesFormateadas));
                        Element stateElement = newDoc.createElement("state");
                        stateElement.appendChild(newDoc.createTextNode(saleElement.getElementsByTagName("state").item(0).getTextContent()));
                        Element departmentElement = newDoc.createElement("department");
                        departmentElement.appendChild(newDoc .createTextNode(saleElement.getElementsByTagName("department").item(0).getTextContent()));

                        nuevoSaleRecord.appendChild(idElement);
                        nuevoSaleRecord.appendChild(firstNameElement);
                        nuevoSaleRecord.appendChild(lastNameElement);
                        nuevoSaleRecord.appendChild(salesElement);
                        nuevoSaleRecord.appendChild(stateElement);
                        nuevoSaleRecord.appendChild(departmentElement);

                        rootElement.appendChild(nuevoSaleRecord);

                        System.out.println('\n'+"Venta anterior en " + deparment + " " + (i + 1) + ": " + ventasAntiguas);
                        System.out.println("Venta nueva en " + deparment + " " + (i + 1) + ": " + newSalesFormateadas);
                    } else {
                        Node nuevoNodo = newDoc.importNode(saleRecord, true);
                        rootElement.appendChild(nuevoNodo);
                    }
                }
            }
            // nuevo documento XML (new_sales.xml)
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(newDoc);
            StreamResult result = new StreamResult(new FileOutputStream("new_sale.xml"));
            transformer.transform(source, result);

            System.out.println('\n'+"Las ventas del departamento '" + deparment + "' han sido incrementadas en " + percentageIncrease + "%.");
            System.out.println("El archivo XML 'new_sales.xml' ha sido generado.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double obtenerPorcentaje(Scanner scanner) {
        double incrementoPorcentaje;
        do {
            System.out.print("Ingrese el porcentaje que desea incrementar (entre 5 y 15): ");
            incrementoPorcentaje = scanner.nextDouble();
            scanner.nextLine();
            if (incrementoPorcentaje < 5 || incrementoPorcentaje > 15) {
                System.out.println("El porcentaje debe estar entre 5% y 15%, por favor vuelve a ingresar el porcentaje.");
            }
        } while (incrementoPorcentaje < 5 || incrementoPorcentaje > 15);
        return incrementoPorcentaje;
    }
}
