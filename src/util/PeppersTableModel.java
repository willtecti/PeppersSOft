package util;

import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class PeppersTableModel extends DefaultTableModel{

	public boolean isCellEditable(int row, int column) {
		return false;

}
	
}	
