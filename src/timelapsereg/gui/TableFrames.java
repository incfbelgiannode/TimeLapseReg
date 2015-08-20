package timelapsereg.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import timelapsereg.process.Data;
import timelapsereg.process.Frame;

public class TableFrames extends JTable {

	private JFrame	frame;
	private Color	colorRed	= new Color(255, 125, 120);
	private Color	colorOK		= new Color(132, 252, 137);
	private Color	colorOrange	= new Color(232, 252, 137);
	private Color	colorNo		= new Color(232, 232, 237);

	private String[]	headers	= new String[] { "#", "Time (ms)", "Filename", "Mean", "Sdtdev", "Content", "Transformation", "RMSE" };
	private Data		data;

	public TableFrames(Data data) {
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
		this.data = data;

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		for (int i = 0; i < headers.length; i++) {
			TableColumn tc = getColumnModel().getColumn(i);
			tc.setCellRenderer(new FramesRenderer());
		}
		update();
	}

	public void update() {
		DefaultTableModel model = (DefaultTableModel) getModel();
		model.getDataVector().removeAllElements();
		for (Frame frame : data.frames)
			addFrame(frame);
		repaint();
	}

	private void addFrame(Frame frame) {
		String[] s = frame.getInformation();
		DefaultTableModel model = (DefaultTableModel) getModel();
		model.addRow(s);
	}

	public void show(int width, int height) {
		frame = new JFrame(data.pathSource);
		frame.add(getPane(width, height));
		frame.pack();
		frame.setVisible(true);
	}

	public JScrollPane getPane(int width, int height) {
		JScrollPane scrollpane = new JScrollPane(this);
		scrollpane.setPreferredSize(new Dimension(width, height));
		return scrollpane;
	}

	public void close() {
		if (frame != null) {
			frame.dispose();
			frame = null;
		}
	}

	public class FramesRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
			Frame.Status status = data.getStatusFrame(Integer.parseInt((String) table.getValueAt(row, 0)));
			if (status == Frame.Status.OK)
				c.setBackground(colorOK);
			else if (status == Frame.Status.NONE)
				c.setBackground(colorNo);
			else if (status == Frame.Status.INVALID_TRANSFORMATION)
				c.setBackground(colorOrange);
			else
				c.setBackground(colorRed);
			return c;
		}
	}

}
