package oop.test.access.example;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

/* A demo show how to use HXTT Access in applet */
public class testApplet extends Applet implements KeyListener,MouseListener
{
    static
    {
        try
        {
            Class.forName("com.hxtt.sql.access.AccessDriver").newInstance();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    Button connectButton=null;
    TextField urlTextField =null;
    TextField queryTextField =null;
    TextArea resultSetTextArea =null;

    Connection con=null;

    public void init()
    {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx=1.0;
        constraints.weighty=0.0;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridwidth = GridBagConstraints.REMAINDER;

        GridBagLayout layout = new GridBagLayout();

        setLayout(layout);
        setFont(new Font("Ariel", Font.PLAIN, 14));
        setBackground(Color.gray);

        connectButton = new Button("  Connect  ");
        connectButton.addMouseListener(this);
        layout.setConstraints(connectButton, constraints);
        add(connectButton);

        add(new Label("Enter URL(e.g., jdbc:Access://localhost:8029/datafiles and jdbc:Access:/c:/datafiles) "));
        urlTextField = new TextField(30);
        urlTextField.setText("jdbc:Access://localhost:8029/.");
        urlTextField.setEditable(true);
        urlTextField.setBackground(Color.white);
        layout.setConstraints(urlTextField, constraints);
        add(urlTextField);

        add(new Label("Enter SQL Query Statement:"));
        queryTextField = new TextField(40);
        queryTextField.setEditable(false);
        queryTextField.setBackground(Color.white);
        queryTextField.addKeyListener(this);
        layout.setConstraints(queryTextField, constraints);
        add(queryTextField);

        Label resultLabel = new Label("Result");
        resultLabel.setFont(new Font("Ariel", Font.PLAIN, 16));
        resultLabel.setForeground(Color.white);
        layout.setConstraints(resultLabel, constraints);
        add(resultLabel);

        constraints.weighty=1.0;
        resultSetTextArea = new TextArea(20,120);
        resultSetTextArea.setEditable(false);
        layout.setConstraints(resultSetTextArea, constraints);
        resultSetTextArea.setForeground(Color.blue);
        resultSetTextArea.setBackground(Color.black);
        add(resultSetTextArea);

        setVisible(true);
    }

    public void keyPressed(KeyEvent ke)
    {
        try
        {
            Object target =ke.getSource();
            if (target == queryTextField && ke.getKeyCode()==KeyEvent.VK_ENTER)
            {
                resultSetTextArea.setText(getResult(queryTextField.getText()));
            }
        }
        catch( Exception e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
            resultSetTextArea.setText(e.getMessage());
        }
    }
    public void keyReleased(KeyEvent e)
    {
    }
    public void keyTyped(KeyEvent e)
    {
    }

    public void mouseClicked(MouseEvent me)
    {
        try
        {
            Object target =me.getSource();
            if (target == connectButton)
            {
                if(con==null)
                {
                    connectButton.setLabel("Connecting");
                    con = DriverManager.getConnection(urlTextField.getText(), "user","password");
                    queryTextField.setEditable(true);
                    connectButton.setLabel("Disconnect");
                }
                else
                {
                    connectButton.setLabel("Connect");
                    con.close();
                    con=null;
                    queryTextField.setEditable(false);
                }
            }
        }
        catch( Exception e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
            resultSetTextArea.setText(e.getMessage());
        }
    }

    public void mousePressed(MouseEvent me)
    {
    }
    public void mouseReleased(MouseEvent me)
    {
    }
    public void mouseEntered(MouseEvent me)
    {
    }
    public void mouseExited(MouseEvent me)
    {
    }

    public String getResult(String queryString)
    {
        try
        {
            StringBuffer strbuff=new StringBuffer(1000);

            Statement stmt = con.createStatement();
            stmt.setMaxRows(25);

            ResultSet rs = stmt.executeQuery(queryString);

            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            int iNumCols = resultSetMetaData.getColumnCount();
            for (int i = 1; i <= iNumCols; i++)
            {
                strbuff.append(resultSetMetaData.getColumnLabel(i));
                strbuff.append("\n");
            }
            while (rs.next())
            {
                for(int i=1;i<=iNumCols;i++)
                    strbuff.append(rs.getObject(i)+"  ");
                strbuff.append("\n");
            }
            stmt.close();
            return strbuff.toString();
        }
        catch( Exception e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }

    public void destroy()
    {
        if(con!=null)
        {
            try
            {
                con.close();
            }
            catch( Exception e)
            {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
    }
}
