# daniel.rovera@gmail.com Institut Curie - Mines Paris Tech
# SUbNetworks RIch in Significant Elements - sunrise

# utilities used in different script: input and output, drawing, computing

import numpy as np
import matplotlib.pyplot as plt
import networkx as nx
import copy
import collections
import sunrise_param as P

def draw_net(net, scores, layout):
    ''' draw the network, negative nodes in blue, positive nodes in red, choose the layout '''
    plt.figure()
    color = list()
    for n in net.nodes:
        if scores[n] > 0:
            color.append('red')
        else:
            color.append('blue')
    pos = layout(net)
    nx.draw_networkx(net, with_labels=True, pos=pos, node_color=color, node_size=100)
    plt.show()


def features(net, mess='', scores=[]):
    ''' Display the feature of network with message according to the presence of a score'''
    print('{} nodes, {} edges'.format(nx.number_of_nodes(net), nx.number_of_edges(net)), mess)
    if len(scores) > 0:
        print('positive number:', sum(scores[n] > 0 for n in net), '/ node number', nx.number_of_nodes(net))
        print('score of positive', sum(scores[n] for n in net if scores[n] > 0), '/ network score',
              sum(scores[n] for n in net))


def display_save(net, names, scores, group_names=None, files=None, if_edge=False):
    ''' display or save in the corresponding files the network in different cases
    normal or reduced with group names and edges '''
    sif_txt, edge_txt = '', 'shared name\tweight\n'
    if if_edge:
        types = nx.get_edge_attributes(net, "type")
        edge_scr = nx.get_edge_attributes(net, "weight")
        for edge in net.edges:
            sif_txt = sif_txt + names[edge[0]] + '\t' + types[edge] + '\t' + names[edge[1]] + '\n'
            edge_txt = edge_txt + names[edge[0]] + ' (' + types[edge] + ') ' + names[edge[1]] + '\t' + str(
                edge_scr[edge]) + '\n'
    else:
        for edge in net.edges: sif_txt = sif_txt + names[edge[0]] + '\tpp\t' + names[edge[1]] + '\n'
    if group_names:
        node_txt = 'shared name\tscore\tname_in_group\n'
    else:
        node_txt = 'shared name\tscore\n'
    if group_names:
        for node in net.nodes:
            node_txt = node_txt + names[node] + '\t' + str(scores[node]) + '\t' + group_names[node] + '\n'
    else:
        for node in net.nodes:
            node_txt = node_txt + names[node] + '\t' + str(scores[node]) + '\n'
    if files:
        out = open(files[0], mode='w')
        out.write(sif_txt)
        out.close()
        out = open(files[1], mode='w')
        out.write(node_txt)
        out.close()
        if if_edge: out = open(files[2], mode='w'); out.write(edge_txt); out.close()
        features(net, mess='written in')
        print(files)
    else:
        print(sif_txt)
        print(node_txt)
        if if_edge: print(edge_txt)
    return


def load_net(files, if_reduce=False):
    ''' load files in 2 cases reduced or not reduced '''
    n_in = open(files[1])
    score_node = list()
    n_in.readline()
    line = n_in.readline()
    while line:
        spl = line.split(P.sep)
        if if_reduce:
            score_node.append((float(spl[1]), spl[0], spl[2]))
        else:
            score_node.append((float(spl[1]), spl[0]))
        line = n_in.readline()
    names, node_name, scores, group_names, n = list(), dict(), np.zeros(len(score_node)), list(), 0
    for sn in score_node:
        names.append(sn[1])
        if if_reduce:
            group_names.append(sn[2])
        node_name[sn[1]] = n
        scores[n] = sn[0]
        n += 1
    if if_reduce:
        net = nx.DiGraph()
    else:
        net = nx.Graph()
    for line in open(files[0]):
        spl = line.split(P.sep)
        if if_reduce:
            net.add_edge(node_name[spl[0]], node_name[spl[2]], type=spl[1], weight=0.0)
        else:
            net.add_edge(node_name[spl[0]], node_name[spl[2]])
    if if_reduce:
        e_in = open(files[2])
        e_in.readline()
        line = e_in.readline()
        while line:
            spl = line.split(P.sep)
            net.edges[node_name[spl[0]], node_name[spl[2]]]['weight'] = float(spl[3])
            line = e_in.readline()
    features(net, mess='loaded from')
    print(files)
    if if_reduce:
        return net, names, scores, group_names
    else:
        return net, names, scores


def save_only_node(nodes, names, group_names, files=None):
    ''' display or save node names of optimal in 2 files nodes and node group'''
    rnode_txt, node_txt, nb = '', '', 0
    for rn in nodes:
        rnode_txt = rnode_txt + names[rn] + '\n'
        gn = group_names[rn].split(';')
        for nn in gn:
            node_txt = node_txt + nn + '\n'
            nb += 1
    if files:
        out = open(files[0], mode='w')
        out.write(node_txt)
        out.close()
        out = open(files[1], mode='w')
        out.write(rnode_txt)
        out.close()
        print(nb, 'nodes', 'and', len(nodes), 'reduced nodes are written in')
        print(files)
    else:
        print('group nodes')
        print(rnode_txt)
        print('nodes')
        print(node_txt)

def is_connected(G):
    ''' check if the network is connected (more performant than the of networkx '''
    n, visited, nodes, root = 1, set(), collections.deque(), nx.utils.arbitrary_element(G)
    visited.add(root)
    nodes.appendleft(root)
    while nodes:
        parent = nodes.pop()
        children = G[parent]
        for child in children:
            if child not in visited:
                visited.add(child)
                nodes.appendleft(child)
                n += 1
    return n == nx.number_of_nodes(G)

def through_between(net, nodes, path_funct, of_path):
    ''' pass by all couples of nodes without order executing the path function '''
    in1 = iter(nodes)
    while True:
        try:
            s = next(in1)
            in2 = copy.copy(in1)
            while True:
                try:
                    t = next(in2)
                    path_funct(net, s, t, of_path)
                except StopIteration:
                    break
        except StopIteration:
            break
    return of_path


def neg_dgr1_remove(net, scores):
    ''' remove recursively the negative nodes with degree 1 '''
    while True:
        dgrs = list(net.degree())
        dgrs.sort(key=lambda k: k[1])
        i, end_rem = 0, True
        while dgrs[i][1] == 1 and i < len(dgrs):
            if scores[dgrs[i][0]] < 0.0:
                net.remove_node(dgrs[i][0])
                end_rem = False
            i += 1
        if end_rem: break
