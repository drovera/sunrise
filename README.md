# sunrise
SUbNetworks RIch in Significant Elements

In java, sunrise is an application of Cytoscape 3.x which searches subnet significantly enriched in
low p-values. An additive score is computed from p-values got by genome-wide association
study or gene expression. These values are matched to the nodes of a protein protein
interaction network. The the highest score subnet gives information about the touched
pathways which gives indications on the involved functions.

Searching is based on a genetic algorithm. the process is :
- Convert p-values into additive scores
- From network and scores create a network which contains the solution
- Search a solution by genetic algorithm
- Refine eventually the found subnet

Several functions are avalaible to make easy the use of sunrise completed by these of Cytoscape.

For more details, refer to the manual and use the tutorial.

In Python, the scripts gives simply an approximation of optimal without any hyper-parameter.
All files are compatible with Cytoscape. See manual and algorithm in sunrise_py.pdf.
