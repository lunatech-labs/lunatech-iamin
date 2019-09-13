package com.lunatech.iamin

import com.lunatech.iamin.components.TodoItem

trait Api {
  def welcomeMsg(name: String): String

  def getAllTodos(): Seq[TodoItem]

  def updateTodo(item: TodoItem): Seq[TodoItem]

  def deleteTodo(itemId: String): Seq[TodoItem]
}
