// This will set the DisplayName & the Description for the latest build for a given Job.
// It takes the following parameters JobName & the Display name for the build.
// You can set the changlist as BUILD_DISPLAYNAME

import hudson.model.Hudson
import hudson.model.*
def jobName

def setBuildDisplayfromJobName (JOBNAME,BUILD_DISPLAYNAME)
{
	// This function will only set the Build Display name.
	def thisJob = hudson.model.Hudson.instance.getItem("${JOBNAME}") 
	
	def jobFullName = thisJob.getFullName()
	def thisProject = hudson.model.Hudson.getInstance().getItemByFullName(jobFullName)
	
	def thisJoblastBuildNumber = thisJob.getNextBuildNumber() - 1
	def thisBuild = thisJob.getBuildByNumber(thisJoblastBuildNumber)		
	
	def buildDisplayName
	if (BUILD_DISPLAYNAME?.trim()){
		buildDisplayName = BUILD_DISPLAYNAME.trim()
	}
	
	manager.listener.logger.println ("Setting the Build Display name - ${buildDisplayName}")
	println ("Setting the Build Display name - ${buildDisplayName}")
	thisBuild.setDisplayName(buildDisplayName)
	
	// Below is bonus as it can set the Description for the build too. It assumes that the Build Display name is the Changlist number & proceeds with that assumption.
	def buildDescription = "Jenkins Build #${thisJoblastBuildNumber} Completed.\nThis ​was ​build ​with ​Changlist ​#${BUILD_DISPLAYNAME}."
	buildDescription = buildDescription.trim()	
	manager.listener.logger.println ("Setting the Build Description\n\t${buildDescription}")
	println ("Setting the Build Description\n${buildDescription}")
	thisBuild.setDescription(buildDescription)
	
	return
}
// You will need to call the function & pass it JobName & the Name that you want to set for Display.
return
