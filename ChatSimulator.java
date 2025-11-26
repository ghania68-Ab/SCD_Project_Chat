import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

// ==========================================
// 1. MESSAGE FACTORY PATTERN (Creational)
// ==========================================

abstract class Message {
    protected String sender;
    protected String content;

    public Message(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    public String getSender() { return sender; }
    public abstract String getDisplayText();
}

class TextMessage extends Message {
    public TextMessage(String sender, String content) {
        super(sender, content);
    }
    @Override
    public String getDisplayText() { return content; }
}

class SystemMessage extends Message {
    public SystemMessage(String sender, String content) {
        super(sender, content);
    }
    @Override
    public String getDisplayText() { return "[SYSTEM]: " + content.toUpperCase(); }
}

class MessageFactory {
    public static Message createMessage(String type, String sender, String content) {
        if (type.equalsIgnoreCase("text")) {
            return new TextMessage(sender, content);
        } else if (type.equalsIgnoreCase("system")) {
            return new SystemMessage(sender, content);
        } else {
            throw new IllegalArgumentException("Unknown message type");
        }
    }
}

// ==========================================
// 2. DECORATOR PATTERN (Structural)
// ==========================================

abstract class MessageDecorator extends Message {
    protected Message wrappedMessage;

    public MessageDecorator(Message message) {
        super(message.getSender(), ""); 
        this.wrappedMessage = message;
    }

    @Override
    public String getDisplayText() {
        return wrappedMessage.getDisplayText();
    }
    
    public Message getWrappedMessage() {
        return wrappedMessage;
    }
}

class TimestampDecorator extends MessageDecorator {
    public TimestampDecorator(Message message) {
        super(message);
    }

    @Override
    public String getDisplayText() {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        return "[" + time + "] " + wrappedMessage.getDisplayText();
    }
}

// ==========================================
// 3. OBSERVER PATTERN (Behavioral)
// ==========================================

interface Observer {
    void update(Message message);
}

class Subject {
    private List<Observer> observers = new ArrayList<>();

    public void attach(Observer observer) { observers.add(observer); }
    public void notifyObservers(Message message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}

// ==========================================
// 4. SINGLETON PATTERN (Creational) & Subject
// ==========================================

class ChatEngine extends Subject {
    private static ChatEngine instance;

    private ChatEngine() { /* Initialization */ }

    public static synchronized ChatEngine getInstance() {
        if (instance == null) {
            instance = new ChatEngine();
        }
        return instance;
    }

    public void sendMessage(Message message) {
        notifyObservers(message);
    }
}

// ==========================================
// 5. BUILDER PATTERN (Creational)
// ==========================================

class ChatSession {
    private String username;
    private String theme;
    public void setUsername(String username) { this.username = username; }
    public void setTheme(String theme) { this.theme = theme; }
    public String getUsername() { return username; }
    public String getTheme() { return theme; }
}

class ChatSessionBuilder {
    private ChatSession session = new ChatSession();
    public ChatSessionBuilder setUsername(String username) {
        session.setUsername(username);
        return this;
    }
    public ChatSessionBuilder setTheme(String theme) {
        session.setTheme(theme);
        return this;
    }
    public ChatSession build() { return session; }
}

// ==========================================
// GUI IMPLEMENTATION (Swing) & Observer
// ==========================================

public class ChatSimulator extends JFrame implements Observer {
    private ChatEngine engine;
    private ChatSession session;
    private JTextPane chatArea;
    private JTextField inputField;
    private JCheckBox timestampCheck;
    private StyledDocument doc;

    public ChatSimulator() {
        engine = ChatEngine.getInstance();
        engine.attach(this); 
        session = new ChatSessionBuilder()
                .setUsername("Student_User")
                .setTheme("Dark Mode")
                .build();
        setupUI();
        startSimulation();
    }

    private void setupUI() {
        setTitle("Java Chat Simulator - Design Patterns");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header: Builder Output
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(51, 51, 51));
        JLabel statusLabel = new JLabel("Logged in as: " + session.getUsername() + " | " + session.getTheme());
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(statusLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Chat Area
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setBackground(new Color(240, 240, 240));
        doc = chatArea.getStyledDocument();
        
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        // Input and Buttons
        inputField = new JTextField();
        JButton sendBtn = new JButton("Send");
        sendBtn.setBackground(new Color(0, 120, 215));
        sendBtn.setForeground(Color.WHITE);
        
        JButton systemBtn = new JButton("System Msg"); 
        systemBtn.setBackground(new Color(215, 0, 0));
        systemBtn.setForeground(Color.WHITE);

        // Actions: Call sendMessage with the correct message type (Factory input)
        ActionListener sendTextAction = e -> sendMessage("text");
        sendBtn.addActionListener(sendTextAction);
        inputField.addActionListener(sendTextAction);
        
        ActionListener sendSystemAction = e -> sendMessage("system"); 
        systemBtn.addActionListener(sendSystemAction);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.add(systemBtn);
        buttonPanel.add(sendBtn);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.EAST);

        // Options Panel
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // Decorator Control
        timestampCheck = new JCheckBox("Apply Timestamp Decorator", true);
        optionsPanel.add(timestampCheck);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputPanel, BorderLayout.CENTER);
        bottomPanel.add(optionsPanel, BorderLayout.SOUTH);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Helper to safely unwrap the Decorator and find the base message
    private Message unwrapMessage(Message message) {
        if (message instanceof MessageDecorator) {
            // Recursively check the wrapped content 
            return unwrapMessage(((MessageDecorator) message).getWrappedMessage()); 
        }
        return message;
    }
    
    private void sendMessage(String messageType) {
        String content = inputField.getText().trim();
        if (content.isEmpty()) return;
        // 1. Factory Pattern: Create base message
        Message msg = MessageFactory.createMessage(messageType, session.getUsername(), content);
        // 2. Decorator Pattern: Wrap the message if checked
        if (timestampCheck.isSelected()) {
            msg = new TimestampDecorator(msg);
        }
        // 3. Singleton Engine: Send the message
        engine.sendMessage(msg);
        inputField.setText("");
    }

    // Observer Pattern: This method receives the message and handles UI update
    @Override
    public void update(Message message) {
        // Use SwingUtilities to ensure thread safety
        SwingUtilities.invokeLater(() -> {
            try {
                SimpleAttributeSet style = new SimpleAttributeSet();
                String displayText = message.getDisplayText() + "\n";
                String sender = message.getSender();
                
                // CRITICAL FIX: Unwrap the message to determine its true type (Factory Product)
                Message baseMessage = unwrapMessage(message); 

                // === FINAL FIX: Coloring logic based on the TRUE type of the base message ===
                
                // 1. Check for System Messages (Factory product) - MUST BE RED (Prioritized Check)
                if (baseMessage instanceof SystemMessage) {
                    StyleConstants.setForeground(style, Color.RED);
                    StyleConstants.setBold(style, true);
                    StyleConstants.setFontSize(style, 14); 
                } 
                // 2. Check for TeacherBot Messages (Observer source) - MUST BE GREEN
                else if (sender.equals("TeacherBot")) {
                    StyleConstants.setForeground(style, new Color(0, 100, 0)); // Dark Green 
                    displayText = sender + ": " + displayText;
                } 
                // 3. Default to current user's normal Text Message - MUST BE BLUE
                else if (sender.equals(session.getUsername())) {
                    StyleConstants.setForeground(style, Color.BLUE);
                }
                
                doc.insertString(doc.getLength(), displayText, style);
                chatArea.setCaretPosition(doc.getLength());
                
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }

    // Background thread to simulate incoming traffic (Observer demonstration)
    private void startSimulation() {
        new Thread(() -> {
            String[] phrases = {
                "Hello! Can you see this?", 
                "The Observer pattern works nicely in Java.", 
                "Don't forget to show the Decorator class.",
                "Is the Singleton thread-safe?"
            };
            Random rand = new Random();
            
            try {
                Thread.sleep(2000);
                for (String phrase : phrases) {
                    Thread.sleep(2000 + rand.nextInt(3000));
                    
                    Message msg = MessageFactory.createMessage("text", "TeacherBot", phrase);
                    // Decorator is also applied to incoming messages
                    msg = new TimestampDecorator(msg);
                    engine.sendMessage(msg);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChatSimulator app = new ChatSimulator();
            app.setVisible(true);
        });
    }
}

