/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 Thomas Cashman
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.mini2Dx.butler.task

import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.mini2Dx.butler.ButlerUtils
import org.mini2Dx.butler.exception.NoBuildException

/**
 * Calls 'butler push'. Will push the game binary corresponding to the OS the task runs on.
 */
class PushTask extends DefaultTask  {
	PushTask() {
		super()
		dependsOn("butlerUpdate")
	}

	@TaskAction
	def pushToItch() {
		String channel = project.getExtensions().findByName('butler').channel;
		File binDirectory = project.getExtensions().findByName('butler').binDirectory;
		
		if(channel == null) {
    		throw new Exception("No channel set!")
		}
		if(binDirectory == null) {
			throw new Exception("No steward binary directory set for " + channel)
		}

		if(!binDirectory.exists()) {
			throw new NoBuildException()
		}
		if(!binDirectory.isDirectory()) {
		    throw new Exception("Provided bin directory not a real directory")
		}
		String user = project.getExtensions().findByName('butler').user
		if(user == null) {
			throw new Exception("User not set in steward configuration")
		}
		String game = project.getExtensions().findByName('butler').game
		if(game == null) {
			throw new Exception("Game not set in steward configuration")
		}
		String deployDetails = user + "/" + game + ":" + channel;

		if(project.getExtensions().findByName('butler').userVersion != null) {
			println "Deploying to itch.io [" + deployDetails + "] with version " + project.getExtensions().findByName('butler').userVersion
			ButlerUtils.execButler(project, "push", binDirectory.getAbsolutePath(), deployDetails, "--userversion", project.getExtensions().findByName('butler').userVersion);
		} else {
			println "Deploying to itch.io [" + deployDetails + "]"
			ButlerUtils.execButler(project, "push", binDirectory.getAbsolutePath(), deployDetails);
		}
	}
}
