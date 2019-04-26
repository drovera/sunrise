######################################################
# Create a score table according to beta uniform model
# from p-value table index by gene/node names
# read and write data in files (inFile, outFile)
######################################################
library(BioNet)

# input file with header: node_name;pvalue,
# file name and separator
inFile<-"D:/Projects/sunrise/test/SFN_9980_p-values.txt"
separator<-";"
#*******

pval<-read.table(inFile,header=TRUE,sep = separator)
pvals<-as.array(pval$pvalue)
names(pvals)=pval$node
fb <- fitBumModel(pvals, plot=TRUE)
fb

# Correction of fb
#fb$lambda<-0.01

# Choice of upper bound of false positive rate
UpperBoundFDR<-0.001
#*******

tau<-fdrThreshold(UpperBoundFDR,fb)
scores<-((fb$a - 1)*(log(pvals)-log(tau)))
print(paste("positive scores: ",length(which(scores>0))))
print(paste("negative scores: ",length(which(scores<0))))

# output file with header updated to be import in Cytoscape,
# complete file name and separator
outFile <- "D:/Projects/sunrise/BioNet/SFN_9980_scr.csv"
separator<-";"
#*******

write.table(scores,outFile,quote=FALSE,dec=".",sep=separator)

#*** complete header of output file to import in Cytoscape
#*** as shared name;score

