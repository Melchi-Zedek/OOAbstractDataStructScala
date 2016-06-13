package mapset.drawing

import collection.mutable
import scalafx.Includes._
import scalafx.scene.Node
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.layout.VBox
import scalafx.scene.control.ComboBox
import scalafx.event.ActionEvent
import scalafx.scene.transform.Rotate
import scalafx.scene.transform.Translate
import scalafx.scene.transform.Scale
import scalafx.scene.transform.Shear

class DrawTransform(d: Drawing) extends Drawable(d) {
  private val _children = mutable.Buffer[Drawable]()
  private var propPanel: Option[Node] = None
  private var transformType = DrawTransform.Translate
  private var value1 = 0.0
  private var value2 = 0.0

  def children = _children

  override def toString = "Transform"

  def addChild(d: Drawable): Unit = {
    _children += d
  }

  def removeChild(d: Drawable): Unit = {
    _children -= d
  }

  def draw(gc: GraphicsContext): Unit = {
    gc.save()
    transformType match {
      case DrawTransform.Rotate => gc.rotate(value1)
      case DrawTransform.Translate => gc.translate(value1, value2)
      case DrawTransform.Scale => gc.scale(value1, value2)
      case DrawTransform.Shear => gc.transform(1.0, value1, value2, 1.0, 0.0, 0.0)
    }
    _children.foreach(_.draw(gc))
    gc.restore()
  }

  def propertiesPanel(): Node = {
    if (propPanel.isEmpty) {
      val panel = new VBox
      val combo = new ComboBox(DrawTransform.values.toSeq)
      combo.onAction = (ae: ActionEvent) => {
        transformType = combo.selectionModel.value.selectedItem.value
        drawing.draw()
      }
      combo.selectionModel.value.select(transformType)
      val v1Field = DrawingMain.labeledTextField("x/theta", value1.toString, s => { value1 = s.toDouble; drawing.draw() })
      val v2Field = DrawingMain.labeledTextField("y", value2.toString, s => { value2 = s.toDouble; drawing.draw() })
      panel.children = List(combo, v1Field, v2Field)
      propPanel = Some(panel)
    }
    propPanel.get
  }
}

object DrawTransform extends Enumeration {
  val Rotate, Scale, Shear, Translate = Value
}