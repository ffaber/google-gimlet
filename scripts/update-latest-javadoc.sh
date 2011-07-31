#!/bin/tcsh

svn delete latest-javadoc
svn ci -m "Removed old Javadocs." latest-javadoc
mvn javadoc:aggregate
svn add latest-javadoc
svn ci -m "Added updated Javadocs." latest-javadoc
