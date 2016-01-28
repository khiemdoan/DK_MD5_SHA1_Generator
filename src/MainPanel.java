
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MainPanel extends JPanel {

    public MainPanel() {
        setSize(600, 500);
        setLayout(null);
        initComponents();
    }

    private JTextArea textArea;
    private JScrollPane scrollTextArea;
    private JTextField textFieldMd5;
    private JTextField textFieldSha1;
    private JButton btnGenerate;
    private JButton btnClearText;
    private JButton btnExit;
    private JButton btnOpen;

    private void initComponents() {
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        scrollTextArea = new JScrollPane();
        scrollTextArea.setBounds(20, 140, 400, 200);
        scrollTextArea.setViewportView(textArea);
        add(scrollTextArea);

        btnGenerate = new JButton("Generate");
        btnGenerate.setBounds(450, 140, 100, 60);
        btnGenerate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                btnGenerateActionPerformed(evt);
            }
        });
        add(btnGenerate);

        btnClearText = new JButton("Clear Text");
        btnClearText.setBounds(450, 220, 100, 50);
        btnClearText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                btnClearTextActionPerformed(evt);
            }
        });
        add(btnClearText);

        btnExit = new JButton("Exit");
        btnExit.setBounds(450, 290, 100, 50);
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });
        add(btnExit);

        textFieldMd5 = new JTextField();
        textFieldMd5.setBounds(160, 360, 300, 30);
        textFieldMd5.setEditable(false);
        add(textFieldMd5);

        textFieldSha1 = new JTextField();
        textFieldSha1.setBounds(160, 410, 300, 30);
        textFieldSha1.setEditable(false);
        add(textFieldSha1);

        Font font = new Font("Serif", Font.PLAIN | Font.ITALIC, 30);
        JLabel label = new JLabel("MD5 & SHA-1 Hash Generator");
        label.setFont(font);
        label.setBounds(100, 30, 400, 50);
        add(label);

        JLabel labelInput = new JLabel("Input:");
        labelInput.setBounds(20, 120, 100, 20);
        add(labelInput);

        JLabel labelMd5 = new JLabel("MD5:");
        labelMd5.setBounds(120, 365, 50, 20);
        add(labelMd5);

        JLabel labelSha1 = new JLabel("SHA-1:");
        labelSha1.setBounds(120, 415, 50, 20);
        add(labelSha1);

        btnOpen = new JButton("Open File");
        btnOpen.setBounds(340, 110, 80, 30);
        btnOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                btnOpenTextActionPerformed(evt);
            }
        });
        add(btnOpen);
    }

    private void btnGenerateActionPerformed(ActionEvent evt) {
        MD5 md5 = new MD5();
        String str = md5.generateToString(textArea.getText());
        textFieldMd5.setText(str);

        SHA1 sha1 = new SHA1();
        str = sha1.generateToString(textArea.getText());
        textFieldSha1.setText(str);
    }

    private void btnClearTextActionPerformed(ActionEvent evt) {
        textArea.setText("");
        textFieldMd5.setText("");
        textFieldSha1.setText("");
    }

    private void btnExitActionPerformed(ActionEvent evt) {
        System.exit(0);
    }

    private void btnOpenTextActionPerformed(ActionEvent evt) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text file", "txt");
        chooser.setFileFilter(filter);
        int choose = JFileChooser.ERROR_OPTION;     // -1
        choose = chooser.showOpenDialog(this);
        if (choose == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                // mở file
                FileInputStream fis = new FileInputStream(file);
                int n = fis.available();
                byte[] buffer = new byte[n];
                fis.read(buffer, 0, n);
                fis.close();
                
                // hiển thị ra text area
                String str = new String(buffer);
                textArea.setText(str);
                
                // tính md5
                MD5 md5 = new MD5();
                textFieldMd5.setText(md5.generateToString(buffer));

                // tính sha1
                SHA1 sha1 = new SHA1();
                textFieldSha1.setText(sha1.generateToString(buffer));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
