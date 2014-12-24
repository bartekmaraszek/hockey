package pl.bmaraszek;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextPane;


public class InfoPanel extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	private static final int WINDOW_WIDTH = 450;
	private static final int WINDOW_HEIGHT = 450;
	
	private JPanel mainContainer, topPanel, textPanel, bottomPanel;
	private JButton closeButton;
	
    public InfoPanel(Image image, String text) {
    	
    	setMainFrame();
    	setMainContainer();
        setTopPanel(image, "Hockey - How to play");
        mainContainer.add(topPanel);
        setTextPanel(text);
        mainContainer.add(textPanel);
        setBottomPanel();
        mainContainer.add(bottomPanel);
        this.add(mainContainer);
        
    }
	
	private void addTopPanelLabel(String label){
        JLabel hint = new JLabel(label);
        hint.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
        topPanel.add(hint);
	}
	
	private void addTopPanelIcon(Image image){
		 ImageIcon icon = new ImageIcon(image);
	        JLabel label = new JLabel(icon);
	        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	        topPanel.add(label, BorderLayout.EAST);
	}
	
	private void setTopPanel(Image image, String text){
		topPanel = new JPanel(new BorderLayout(0, 0));
		topPanel.setMaximumSize(new Dimension(450, 0));
		
		addTopPanelIcon(image);
        addTopPanelLabel(text);
		
		JSeparator separator = new JSeparator();
        separator.setForeground(Color.gray);

        topPanel.add(separator, BorderLayout.SOUTH);
	}
	
	private void setMainContainer(){
		mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        
	}
	
	private void setTextPanel(String text){
		textPanel = new JPanel(new BorderLayout());
        textPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        JTextPane pane = new JTextPane();

        pane.setContentType("text/html");
        pane.setText(text);
        pane.setEditable(false);
        textPanel.add(pane);
	}
	
	private void setMainFrame(){
		setTitle("Hockey - help");
        setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
	}
	
	private void setBottomPanel(){
		bottomPanel = new JPanel();

        closeButton = new JButton("Close");
        closeButton.addActionListener(this);

        bottomPanel.add(closeButton);
        bottomPanel.setMaximumSize(new Dimension(450, 0));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == closeButton){
			this.setVisible(false);
		}
	}

}
