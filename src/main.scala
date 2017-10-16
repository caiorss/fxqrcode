package fxqrcode 

/// Enable type-safe duck-typing or structural typing
import scala.language.reflectiveCalls

import javax.swing.{
   JFrame, JTextArea, JPanel, BorderFactory
  ,JLabel, JScrollPane, JButton, ImageIcon
}
import java.awt.BorderLayout


object FunctionToListener{
  import java.awt.event.{ActionEvent, ActionListener}

  type HasActionListener = {
    def addActionListener(act: ActionListener)
  }

  type HasDocListener = {
    def getDocument(): javax.swing.text.Document
  }

  /** Add extensions methods .onAction to any instance of class that has
      the method .addActionListener such as JButton.
    */
  
  def onAction(act: HasActionListener)(handler: () => Unit) = {
    val a = new ActionListener(){
      def actionPerformed(evt: ActionEvent) = handler()
    }
    act.addActionListener(a)
  }

  /** Add extension method to any instance of class that has
      .getDocument method such as JTextField or JTextArea.
    */
  def onTextChange(act: HasDocListener)(handler: () => Unit) = {
    val listener = new javax.swing.event.DocumentListener(){
      def changedUpdate(arg: javax.swing.event.DocumentEvent) = handler()
      def insertUpdate (arg: javax.swing.event.DocumentEvent) = handler()
      def removeUpdate (arg: javax.swing.event.DocumentEvent) = handler()
    }
    act.getDocument().addDocumentListener(listener)
  }


  def onResize(comp: java.awt.Component)(handler: => Unit) = {
    val listener = new java.awt.event.ComponentAdapter(){
      override def componentResized(evt: java.awt.event.ComponentEvent){
        handler
      }
    }
    comp.addComponentListener(listener)
  }


} //------- End of object FunctionToListener ------- //


import FunctionToListener._

class MainWindow(
  width: Int = 579,
  height: Int = 535,
  exitOnClose: Boolean = false

) extends JFrame {

  val tarea    = new JTextArea()
  val btnClear = new JButton("Clear")
  val bgColor = java.awt.Color.WHITE 
  // PictureBox where the QRcode is shown
  val pbox = new JLabel()

  init()


  private def init(){

    // ------ Make GUI Layout ------- // 

    val topPanel    = new JPanel()
    val buttonPanel = new JPanel()
    buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT))
    tarea.setRows(10)
    buttonPanel.add(btnClear)
    buttonPanel.setBackground(bgColor)

    topPanel.setLayout(new BorderLayout())
    topPanel.add(new JLabel("Input text"), BorderLayout.NORTH)
    topPanel.add(new JScrollPane(tarea)  , BorderLayout.CENTER)
    topPanel.add(buttonPanel,              BorderLayout.SOUTH)
    topPanel.setBackground(bgColor)

    val mainPanel = new JPanel()
    mainPanel.setLayout(new BorderLayout())
    mainPanel.setBackground(bgColor)
    mainPanel.add(topPanel, BorderLayout.NORTH)
    mainPanel.add(pbox,     BorderLayout.CENTER)
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10))

    this.setContentPane(mainPanel)
    this.setTitle("Fxqrcode")
    this.setSize(width, height)


    // ------ Set Event Handlers ------- //

    if (exitOnClose)
      this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE)

    onTextChange(tarea){ this.makeQRcode _ }

    onResize(this){ this.makeQRcode _ }

    onAction(btnClear){() =>
      this.clear()
    }

  }

  def getText() = tarea.getText()
  def setText(text: String) = tarea.setText(text)
  def clear() = tarea.setText("")


  def makeQRcode() = {
    val text = this.getText()
    if (text != "")
      this.generateQRcode(text)
    else
      this.generateQRcode("empty")
  }

  def generateQRcode(text: String, margin: Int = 200) = {
    val w = pbox.getWidth()  - margin
    val h = pbox.getHeight() - margin
    val bimg = QRCode.writeToImage(text, width = w, height = w)
    pbox.setIcon(new ImageIcon(bimg))
  }

} // End of class 

object Main{

  def main(args: Array[String]){
    val gui = new MainWindow(exitOnClose = true)
    gui.setVisible(true)
    gui.makeQRcode()
  }

}
