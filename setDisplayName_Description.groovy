// This code should be added in the Groovy post build section
// This is generic code that is for the Continues delivery jobs Smoke & Full Run
// This will do the following thing
// 1. Retrieve the Name of the JOB.
// 2. It will update the Description & the Display Name which is passed.
// Written by Rohan Khale.


import hudson.model.Hudson
import hudson.model.*

def jobNamePassed
def jobLog
def sepBuildNumber
def sepBranchName


def getCurrentJobName () {
	// This function returns the name of the current Job
	// This returns something like 'Thread[Executor #0 for 192.168.1.10 : executing Test with a Space in the Name #123,5,main].'
	// Now you need to split this & extract only the Job name.
	def build = Thread.currentThread().toString()	
	def jobName = build.split("executing")
	jobName = jobName[1].split("#")
	jobName = jobName[0]
	if (jobName.size()!=0){
		jobName = jobName.trim()
		return "${jobName}"
	}
	return null
}


def getCurrentConsoleLogfromJobName (JOBNAME){
	// This function will return the current builds log.
	def jobName = JOBNAME
	def returnString
	def thisJob = hudson.model.Hudson.instance.getItem("${JOBNAME}")
	def jobFullName = thisJob.getFullName()
	def thisJoblastBuildNumber = thisJob.getNextBuildNumber() - 1
	def thisBuild = thisJob.getBuildByNumber(thisJoblastBuildNumber)
	if (thisBuild.log?.trim()){
		returnString = thisBuild.log
	}	
	return returnString
}


def isBuildSuccessfullyDownloaded (BUILDLOG){
	// This function will determine if the Build was successfully downloaded or not.
	// If the build was successfully downloaded, it will return true else false.
	def returnBool
	def buildLog = BUILDLOG
	def failureMsg = "No new testable build available. Shutting down Test."
	if (buildLog?.trim()) {
		if (buildLog.contains("${failureMsg}")){			
			returnBool = false
		}
		else {			
			returnBool = true
		}
	}
	else {
		manager.listener.logger.println("\nBuild log empty or is blank.")
		println("\nBuild log empty or is blank.")
		returnStr false
	}
	return returnBool
}


def getBuildNumFromBuildLog (BUILDLOG){
	// This function will retrive the build number from the build log.
	// This will search for string "Build to Download                 :-- " e.g. "Build to Download                 :-- 103"
	// At the end of this string is the build number.
	def returnStr
	def buildLog=BUILDLOG	
	def logLineSuccess = "Build to Download                 :-- "
	def buildLine = buildLog.split("\n")
	buildLine.any { LINE ->
		if (LINE.contains("${logLineSuccess}")){			
			logLineSuccess = LINE
			return true
		}
		else {
			return
		}
	}
	if (logLineSuccess?.trim()){
		//manager.listener.logger.println("\nFound ${logLineSuccess}")
		//println("\nFound ${logLineSuccess}")
		buildLog = logLineSuccess.split(":--")
		returnStr = buildLog[buildLog.size() - 1]
		returnStr = returnStr.trim()		
	}	
	return returnStr
}

def getBranchFromBuildLog (BUILDLOG){
	// This function will retrive the branch from the build log.
	// This will search for string "Branch                            :-- " e.g. "Branch                            :-- Agile"
	// At the end of this string is the build number.
	def returnStr
	def buildLog=BUILDLOG	
	def logLineSuccess = "Branch                            :-- "
	def buildLine = buildLog.split("\n")
	buildLine.any { LINE ->
		if (LINE.contains("${logLineSuccess}")){			
			logLineSuccess = LINE
			return true
		}
		else {
			return
		}
	}
	if (logLineSuccess?.trim()){
		//manager.listener.logger.println("\nFound ${logLineSuccess}")
		//println("\nFound ${logLineSuccess}")
		buildLog = logLineSuccess.split(":--")
		returnStr = buildLog[buildLog.size() - 1]
		returnStr = returnStr.trim()		
	}	
	return returnStr
}


def setBuildDisplayNameFromJobName(JOBNAME,BUILD_DISPLAYNAME,BRANCH_NAME){
	def thisJob = hudson.model.Hudson.instance.getItem("${JOBNAME}") 
	def jobFullName = thisJob.getFullName()
	def thisJoblastBuildNumber = thisJob.getNextBuildNumber() - 1
	def thisBuild = thisJob.getBuildByNumber(thisJoblastBuildNumber)
	def thisProject = hudson.model.Hudson.getInstance().getItemByFullName(jobFullName)
	def buildDescription = ""	
	def buildDisplayName = BUILD_DISPLAYNAME
	def branchName = BRANCH_NAME
	
	manager.listener.logger.println ("\nSetting the Build Display name - ${buildDisplayName}")
	println ("\nSetting the Build Display name - ${buildDisplayName}")
	thisBuild.setDisplayName(buildDisplayName)
	
	if (branchName.size()!=0){
		branchName = branchName.trim()
		manager.listener.logger.println ("\nSetting the Build Display name - ${branchName}")
		println ("\nSetting the Build Display name - ${branchName}")
		buildDescription = "Build ${BUILD_DISPLAYNAME} for ${branchName} branch successfully downloaded."
	}
	else{
		buildDescription = "Build ${BUILD_DISPLAYNAME} successfully downloaded."
	}

	def downStreamJob = thisProject.getDownstreamProjects()
	if (!downStreamJob.empty){
		def downStreamJobName = downStreamJob[0].getName()
		def downStreamJobBuildNumber = downStreamJob[0].getNextBuildNumber()
		buildDescription = buildDescription + "\nDownstream Build - ${downStreamJobName} - Build #${downStreamJobBuildNumber} will be triggered."
	}
	buildDescription = buildDescription.trim()
	manager.listener.logger.println ("Setting the Build Description\n${buildDescription}")
	println ("Setting the Build Description\n${buildDescription}")
	thisBuild.setDescription(buildDescription)
	return
}


def setFailedMsgFromJobName(JOBNAME,JOBLOG){
	def thisJob = hudson.model.Hudson.instance.getItem("${JOBNAME}") 
	def jobFullName = thisJob.getFullName()
	def thisJoblastBuildNumber = thisJob.getNextBuildNumber() - 1
	def thisBuild = thisJob.getBuildByNumber(thisJoblastBuildNumber)
	def thisProject = hudson.model.Hudson.getInstance().getItemByFullName(jobFullName)
	def buildDescription = "No new testable build available."
	def buildLog=JOBLOG
	def searchline1 = "Last successfully tested build for"
	def buildLine = buildLog.split("\n")
	buildLine.any { LINE ->
		if (LINE.contains("${searchline1}")){			
			searchline1 = LINE
			return true
		}
		else {
			return
		}
	}
	if (searchline1?.trim()){
		//manager.listener.logger.println("\nFound ${searchline1}")
		//println("\nFound ${searchline1}")
		buildLog = searchline1.split(":")
		returnStr = buildLog[buildLog.size() - 1]
		returnStr = returnStr.trim()		
	}
	buildDescription = buildDescription + "\n" + returnStr + "."
	thisBuild.setDescription(buildDescription)
	return
}

// MAIN
jobNamePassed = getCurrentJobName()
if (jobNamePassed?.trim()){
	manager.listener.logger.println("\nCurrent Job name is : ${jobNamePassed}.")
	println("\nCurrent Job name is : ${jobNamePassed}.")
}
jobLog = getCurrentConsoleLogfromJobName(jobNamePassed)
if (isBuildSuccessfullyDownloaded (jobLog)){
	sepBuildNumber = ""
	sepBranchName = ""
	
	manager.listener.logger.println("\nBuild Successfully downloaded.")
	println("\nBuild Successfully downloaded.")
	sepBuildNumber = getBuildNumFromBuildLog(jobLog)
	if (sepBuildNumber?.trim()){
		manager.listener.logger.println("\tBuild Number ${sepBuildNumber}.")
		println("\tBuild Number ${sepBuildNumber}.")
	}
	else {
		manager.listener.logger.println("\nBuild Number returned is blank or null.")
		println("\nBuild Number returned is blank or null.")
	}
	
	sepBranchName = getBranchFromBuildLog(jobLog)
	if (sepBranchName?.trim()){
		manager.listener.logger.println("\nBranch Name ${sepBranchName}.")
		println("\nBranch Number ${sepBranchName}.")		
	}
	else {
		manager.listener.logger.println("\nBranch Name returned is blank or null.")
		println("\nBranch Name returned is blank or null.")
	}
	setBuildDisplayNameFromJobName (jobNamePassed,sepBuildNumber,sepBranchName)
}
else {
	manager.listener.logger.println("\nNo testable Build found")
	println("\nNo testable Build found")	
	setFailedMsgFromJobName (jobNamePassed,jobLog)
}
return true