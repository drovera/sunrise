# daniel.rovera@gmail.com Institut Curie - Mines Paris Tech
# SUbNetworks RIch in Significant Elements - sunrise

# Common parameters used in different scripts

# *** to input ***

# common directory
dir = 'your directory'

# network names
net_name = 'your name of network'

# input and output choices
keep_optimal = False  # save result in ofiles
draw_optimal = True  # draw the found solution
sep = None  # separator of fields in file, if None, separators by default in Python

# *** to input end ***

# build names of files in function of network name (see document)
files = (dir + net_name + '.sif', dir + net_name + '-n.txt')
rfiles = (dir + net_name + '_r.sif', dir + net_name + '_r-n.txt', dir + net_name + '_r-e.txt')
ofiles = (dir + net_name + '_o-n.txt', dir + net_name + '_o-r.txt')


