package pl.bmaraszek;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OkCancelDialog extends Dialog implements ActionListener {

	private static final long serialVersionUID = 3688012281377236152L;
	private Button ok, cancel;
	private TextField text;
	private String data;

	public OkCancelDialog(Frame hostFrame, String title, boolean dModal) {
		super(hostFrame, title, dModal);
		setSize(280, 100);
		setLayout(new FlowLayout());
		text = new TextField(30);
		add(text);
		ok = new Button("OK");
		add(ok);
		ok.addActionListener((ActionListener) this);
		cancel = new Button("Cancel");
		add(cancel);
		cancel.addActionListener(this);
		setData(new String(""));
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getData() {
		return data;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == ok) {
			data = text.getText();
		} else {
			data = "";
		}
		setVisible(false);
	}

}
