// The function getCurrentJobName will return the JobName of the current job.
// If it cannot find the JobName it will return Null
// If used as a part of post-build, this will automatically retrive the name of the current Job.
// Written by Rohan Khale.

import hudson.model.Hudson
import hudson.model.*
def jobName

def getCurrentJobName () {
	// This function gets the name of the current Job
	def build = Thread.currentThread().toString()
	// This returns something like 'Thread[Executor #0 for 192.168.1.10 : executing Test with a Space in the Name #123,5,main].'
	// Now you need to split this & extract only the Job name.
	def jobName = build.split("executing")
	jobName = jobName[1].split("#")
	jobName = jobName[0]
	if (jobName.size()!=0){
		jobName = jobName.trim()
		return "${jobName}"
	}
	return null
}

jobName = getCurrentJobName()
if (jobName?.trim()){
	manager.listener.logger.println("\nCurrent Job name is : ${jobName}.\n")
	println("\nCurrent Job name is : ${jobName}.\n")
}
return
