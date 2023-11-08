package controller;

import view.ExpenseTrackerView;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import model.ExpenseTrackerModel;
import model.Transaction;
import model.Filter.TransactionFilter;

public class ExpenseTrackerController {
  
  private ExpenseTrackerModel model;
  private ExpenseTrackerView view;
  /** 
   * The Controller is applying the Strategy design pattern.
   * This is the has-a relationship with the Strategy class 
   * being used in the applyFilter method.
   */
  private TransactionFilter filter;

  public ExpenseTrackerController(ExpenseTrackerModel model, ExpenseTrackerView view) {
    this.model = model;
    this.view = view;
  }

  public void setFilter(TransactionFilter filter) {
    // Sets the Strategy class being used in the applyFilter method.
    this.filter = filter;
  }

  public void refresh() {
    List<Transaction> transactions = model.getTransactions();
    view.refreshTable(transactions);
  }

  public boolean addTransaction(double amount, String category) {
    if (!InputValidation.isValidAmount(amount)) {
      return false;
    }
    if (!InputValidation.isValidCategory(category)) {
      return false;
    }
    
    Transaction t = new Transaction(amount, category);
    model.addTransaction(t);
    view.getTableModel().addRow(new Object[]{t.getAmount(), t.getCategory(), t.getTimestamp()});
    refresh();
    return true;
  }

  public void applyFilter() {
    //null check for filter
    if(filter!=null){
      // Use the Strategy class to perform the desired filtering
      List<Transaction> transactions = model.getTransactions();
      List<Transaction> filteredTransactions = filter.filter(transactions);
      List<Integer> rowIndexes = new ArrayList<>();
      for (Transaction t : filteredTransactions) {
        int rowIndex = transactions.indexOf(t);
        if (rowIndex != -1) {
          rowIndexes.add(rowIndex);
        }
      }
      view.highlightRows(rowIndexes);
    }
    else{
      JOptionPane.showMessageDialog(view, "No filter applied");
      view.toFront();}

  }
//   public void undoTransaction() {
//     List<Transaction> transactions = model.getTransactions();

//     if (!transactions.isEmpty()) {
//         // Remove the last transaction from the list
//         Transaction removedTransaction = transactions.remove(transactions.size() - 1);

//         // Update the model and view
//         view.getTableModel().removeRow(transactions.size());
//         model.removeTransaction(removedTransaction);

//         // Refresh the view to update the table and total cost
//         refresh();
//     }
// }
public void undoSelectedTransaction(int rowIndex) {
  if (rowIndex >= 0) {
      List<Transaction> transactions = model.getTransactions();
      System.out.println(rowIndex);
      System.out.println(transactions.size());
      if (rowIndex <= transactions.size()) {
          Transaction removedTransaction = transactions.get(rowIndex);
          model.removeTransaction(removedTransaction); // Remove the selected transaction from the model
          view.getTableModel().removeRow(rowIndex); // Remove the corresponding row from the table
          view.refreshTable(model.getTransactions()); // Update the table view
          view.setUndoEnabled(false); // Disable the Undo button
      }

  }
}
}
