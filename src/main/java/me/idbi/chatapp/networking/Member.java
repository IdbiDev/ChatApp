package me.idbi.chatapp.networking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.Main;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Member implements Serializable {
    private UUID uniqueId;
    private String name;
    private String displayName;
    private List<Room> rooms;
    private Map<UUID, String> passwords;

    public Map<UUID, String> loadPasswords() {
        File f = new File(System.getenv("APPDATA") + "/ChatApp/passwords.xml");
        if(!f.exists()) return new HashMap<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(f);

            NodeList nodeList = document.getElementsByTagName("passwords");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node nNode = nodeList.item(i);

                if(nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    Main.debug(eElement.getElementsByTagName("uuid").item(0).getTextContent());
                    Main.debug(eElement.getElementsByTagName("password").item(0).getTextContent());
                }
            }

        } catch (IOException | ParserConfigurationException e) {

        } catch (SAXException e) {
            throw new RuntimeException(e);
        }

        return new HashMap<>();
    }

    public void savePasswords() {
        try {
            File directory = new File(System.getenv("APPDATA") + "/ChatApp");
            if(!directory.exists()) {
                directory.mkdirs();
            }
            File f = new File(System.getenv("APPDATA") + "/ChatApp/passwords.xml");
            if(!f.exists()) {
                f.createNewFile();
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc = builder.newDocument();
            Element psws = doc.createElement("passwords");
            doc.appendChild(psws);

            for (Map.Entry<UUID, String> psw : this.passwords.entrySet()) {
                Element room = doc.createElement("room");

                Element uuid = doc.createElement("uuid");
                uuid.setTextContent(psw.getKey().toString());

                Element password = doc.createElement("password");
                password.setTextContent(psw.getValue());

                room.appendChild(uuid);
                room.appendChild(password);
                psws.appendChild(room);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            StreamResult result = new StreamResult(f);
            transformer.transform(source, result);
        } catch (IOException | ParserConfigurationException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }
}
