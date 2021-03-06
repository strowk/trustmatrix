/*-
 * #%L
 * trustmatrix-main
 * %%
 * Copyright (C) 2017 Timur Sultanaev
 * %%
 * Licensed under the MIT license.
 * See LICENSE file in the root of project for details.
 * #L%
 */
package com.trustmatrix

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage


class MainApp : Application() {
    override fun start(primaryStage: Stage) {
        val root = FXMLLoader.load<Parent>(javaClass.getResource("desktop.fxml"))
        primaryStage.title = "Trust Matrix Evolution"
        primaryStage.scene = Scene(root, 1000.0, 500.0)
        primaryStage.show()
    }


    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(MainApp::class.java)
            println("")
        }
    }
}
