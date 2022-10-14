# daniel.rovera@gmail.com Institut Curie - Mines Paris Tech
# SUbNetworks RIch in Significant Elements - sunrise

# Search approximation of optimum by metric closure and minimum spanning tree


import networkx as nx
import sunrise_utils as U
import sunrise_param as P


def path_funct(net, s, t, of_path):
    ''' function creating a list of the shortest path'''
    path = nx.shortest_path(net, s, t, weight='weight')
    of_path.append(path)


def get_weight_path(net, scores):
    ''' create the list of weight and the shortest path and cut the list when all positive are included '''
    pos_nodes = {n for n in net.nodes if scores[n] > 0}
    pos_nb = len(pos_nodes)
    paths = U.through_between(net, pos_nodes, path_funct, list())
    weight_path = list()
    for path in paths:
        weight_path.append((nx.path_weight(net, path, 'weight'), path))
    weight_path.sort()
    pos_set, n, pnb = set(), 1, 0
    ipath = iter(weight_path)
    while pnb < pos_nb:
        pos_set = pos_set.union(pos_nodes.intersection(next(ipath)[1]))
        n, pnb = n + 1, len(pos_set)
    weight_path = weight_path[:n]
    return weight_path


def opt_by_shortest_path(net, scores):
    ''' search the subnetwork with maximal score bu browsing the sorted list of weight and the shortest path '''
    print('List the shortest paths between positive nodes')
    weight_path = get_weight_path(net, scores)
    print(len(weight_path), 'weighted shortest paths')
    print('Browse the sorted shortest paths')
    pos_set, node_set, max_node, max_scr = set(), set(), None, 0.0
    for wp in weight_path:
        node_set.update(wp[1])
        score = sum(scores[n] for n in node_set)
        if score > max_scr:
            if U.is_connected(net.subgraph(node_set)):
                max_node, max_scr = node_set.copy(), score
    return max_node


def display_opt():
    ''' display, draw or save the approximation of optimal, see options in parameters '''
    net, names, scores, group_names = U.load_net(P.rfiles, if_reduce=True)
    max_node = opt_by_shortest_path(net, scores)
    U.features(net, scores=scores, mess='in initial network: ' + P.net_name)
    print('\n*** result ***')
    max_net = net.subgraph(max_node)
    U.features(max_net, scores=scores, mess='in found subnetwork from: ' + P.net_name)
    print()
    if P.draw_optimal:
        U.draw_net(max_net, scores, nx.spring_layout)
    if P.keep_optimal:
        U.save_only_node(max_net.nodes, names, group_names, files=P.ofiles)
    else:
        U.save_only_node(max_net.nodes, names, group_names)


if __name__ == "__main__":
    display_opt()
