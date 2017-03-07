// Groovy script to find out all the projects that have not been executed for the last n days.
// Option to disable the project
// author : Rohan Khale

// list jobs not run in the last N days / last N months
import groovy.time.TimeCategory

use ( TimeCategory ) {
  // e.g. find jobs not run in last 3 months
  sometimeago = (new Date() - 3.months)
}

def projects
def jobName
def lastBuild
def buildNumber
def buildName
def	status
def	lastRun
def	notBuildfor
def	isProjectDisabled

// If you want to disable the project, set it to true. if not, set it false.
def disableProject = false


projects = jenkins.model.Jenkins.instance.projects.findAll()

for (p in  projects) {
	jobName = p.getFullDisplayName()
	lastBuild = p.getLastBuild()	
	buildNumber = lastBuild.number
	buildName = lastBuild.getFullDisplayName()
	status = lastBuild.getBuildStatusSummary().message
	lastRun = lastBuild.getTime()
	notBuildfor = groovy.time.TimeCategory.minus( new Date(), lastBuild.getTime() )
	isProjectDisabled = p.isDisabled() 
	
	if ( buildNumber == 0) {
		println "\nJOB : ${jobName}"
		println "     --> NO BUILDS.\n"
		}
	
	if (lastRun < sometimeago) {
		println "\nJOB: ${jobName} | Is Project Disabled: ${isProjectDisabled}"
		println "Last Build Number: ${buildNumber} | Last run on: ${lastRun} | Not Build for: ${notBuildfor} | Status: ${status}.\n"
		if (disableProject == true && isProjectDisabled == false){
			println "\nSetting the status for the project ${jobName} to disable."
			p.disable()
			isProjectDisabled = p.isDisabled() 
			println "Current Project Status is updated to disabled = ${isProjectDisabled}"
			}
		}
	}
return