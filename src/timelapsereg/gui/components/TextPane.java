package timelapsereg.gui.components;

import java.awt.Dimension;

import javax.swing.JEditorPane;

/**
 * This class extends the Java JEditorPane to make a easy to use panel to
 * display HTML information.
 * 
 * @author Daniel Sage, Biomedical Imaging Group, EPFL, Lausanne, Switzerland.
 * 
 */
public class TextPane extends JEditorPane {

	public TextPane(String content) {
		String html = "";
		html += "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2//EN\">\n";
		html += "<html><head>\n";
		html += "<style>body {font-family:verdana arial;}</style>\n";
		html += "<style>h1 {text-align:center; font-size:1.0em; font-weight:bold; padding:1px; margin:2px;}</style>\n";
		html += "<style>h2 {text-align:center; font-size:1.0em; font-weight:italic; padding:1px; margin:2px;}</style>\n";
		html += "<style>h3 {text-align:center; font-size:1.0em; font-weight:bold; padding:1px; margin:2px;}</style>\n";
		html += "<style>p {font-size:1.0em; padding:1px; margin:2px;}</style>\n";

		html += "</head>\n";
		html += "<body>\n";
		html += content;
		html += "</body></html>\n";
		setPreferredSize(new Dimension(200, 200));
		setEditable(false);
		setContentType("text/html; charset=ISO-8859-1");
		setText(html);
	}
}
