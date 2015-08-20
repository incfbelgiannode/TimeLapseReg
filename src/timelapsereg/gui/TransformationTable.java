package timelapsereg.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import timelapsereg.Transformation;

public class TransformationTable extends JTable {

	private Color				colorOddRow		= new Color(245, 245, 250);
	private Color				colorEvenRow	= new Color(232, 232, 237);

	private String[]			headers			= new String[] {"dx", "dy", "Angle" };
	private ArrayList<Transformation> transformations;
	
	public TransformationTable(ArrayList<Transformation> transformations) {
		super();
		DefaultTableModel tableModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		setModel(tableModel);
		DefaultTableModel model = (DefaultTableModel) getModel();
		model.setColumnIdentifiers(headers);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setRowSelectionAllowed(true);
		this.transformations = transformations;
		
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		for (int i = 0; i < headers.length; i++) {
			TableColumn tc = getColumnModel().getColumn(i);
			tc.setCellRenderer(new AlternatedRowRenderer());
		}
		update();
	}

	public void show(int width, int height) {
		JFrame frame = new JFrame("Transformations");
		frame.add(getPane(width, height));
		frame.pack();
		frame.setVisible(true);
	}

	public JScrollPane getPane(int width, int height) {
		JScrollPane scrollpane = new JScrollPane(this);
		scrollpane.setPreferredSize(new Dimension(width, height));
		return scrollpane;
	}

	public void update() {
		DefaultTableModel model = (DefaultTableModel) getModel();
		model.getDataVector().removeAllElements();
		for (Transformation t : transformations)
			addTransformation(t);
		repaint();
	}

	private void addTransformation(Transformation t) {
		String[] s = new String[] {""+t.dx, ""+t.dy, ""+t.angle};
		DefaultTableModel model = (DefaultTableModel) getModel();
		model.addRow(s);
	}

	public class AlternatedRowRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
			if (!isSelected) c.setBackground(row % 2 == 0 ? colorEvenRow : colorOddRow);
			return c;
		}
	}
}
