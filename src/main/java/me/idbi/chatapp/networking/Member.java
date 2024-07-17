package me.idbi.chatapp.networking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.idbi.chatapp.Main;
import me.idbi.chatapp.networkside.Client;
import me.idbi.chatapp.networkside.IllegalNetworkSideException;
import me.idbi.chatapp.networkside.Server;
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

    @Client
    public Map<UUID, String> loadPasswords() {
        if(Main.isServer()) {
            throw new IllegalNetworkSideException("This function is only callable from client-side");
        }

        File f = new File(System.getenv("APPDATA") + "/ChatApp/passwords.xml");
        if(!f.exists()) return new HashMap<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(f);
            document.getDocumentElement().normalize();

            NodeList roomList = document.getElementsByTagName("room");

            Map<UUID, String> passwords = new HashMap<>();
            for (int temp = 0; temp < roomList.getLength(); temp++) {
                Element roomElement = (Element) roomList.item(temp);

                String uuid = roomElement.getElementsByTagName("uuid").item(0).getTextContent();
                String password = roomElement.getElementsByTagName("password").item(0).getTextContent();

                passwords.put(UUID.fromString(uuid), password);
            }

            this.passwords.clear();
            this.passwords.putAll(passwords);

            return passwords;
        } catch (IOException | ParserConfigurationException | SAXException e) {
            return new HashMap<>();
        }
    }

    @Client
    public void savePasswords() {
        if(Main.isServer()) {
            throw new IllegalNetworkSideException("This function is only callable from client-side");
        }

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

            // Load passwords
//            for (int i = 0; i < 50; i++) {
//                this.passwords.put(UUID.randomUUID(), "Cica" +  i);
//            }

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
    @Client
    @Server
    public boolean hasPassword(Room room) {
        if(!this.passwords.containsKey(room.getUniqueId())) return false;

        if(!room.hasPassword()) return true;
        return this.passwords.get(room.getUniqueId()).equals(room.getPassword());
    }

    @Client
    @Server
    public List<Room> getPermanentRooms() {
        return this.rooms.stream().filter(Room::isPermanent).toList();
    }
}
