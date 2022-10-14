# daniel.rovera@gmail.com Institut Curie - Mines Paris Tech
# SUbNetworks RIch in Significant Elements - sunrise

# Build a reduced network to prepare the search:
# group positive, double and weight edges, keep the shortest path

import networkx as nx
import sunrise_param as P
import sunrise_utils as U


def group_positive(net, names, scores):
    ''' group linked positive nodes and remove unuseful edges '''
    positive = [n for n in net.nodes if scores[n] > 0]
    groups = {n: n for n in net.nodes if scores[n] <= 0}
    connected = nx.connected_components(nx.subgraph(net, positive))
    in_group = set()
    for cc in connected:
        for n in cc:
            groups[n] = min(cc)
            if len(cc) > 1:
                in_group.add(n)
    group_names = names.copy()
    for n in in_group:
        if n != groups[n]:
            scores[groups[n]] += scores[n]
            group_names[groups[n]] = group_names[groups[n]] + ';' + group_names[n]
        else:
            names[n] = '{' + names[n] + '}'
    compacted = nx.Graph()
    [compacted.add_edge(groups[e[0]], groups[e[1]]) for e in net.edges]
    compacted.remove_edges_from(nx.selfloop_edges(compacted))
    U.neg_dgr1_remove(compacted, scores)
    return compacted, group_names


def double_edge(net, scores):
    ''' double edges and attribute weight (see algorithm) '''
    double = nx.DiGraph()
    for e in net.edges:
        if scores[e[0]] > 0:
            double.add_edge(e[0], e[1], type='toneg', weight=-scores[e[1]])
            double.add_edge(e[1], e[0], type='topos', weight=0.0)
        elif scores[e[1]] > 0:
            double.add_edge(e[0], e[1], type='topos', weight=0.0)
            double.add_edge(e[1], e[0], type='toneg', weight=-scores[e[0]])
        else:
            double.add_edge(e[0], e[1], type='toneg', weight=-scores[e[1]])
            double.add_edge(e[1], e[0], type='toneg', weight=-scores[e[0]])
    return double


def keep_nodes(net, s, t, of_path):
    ''' path function keeping only nodes in the shortest path '''
    spt = nx.shortest_path(net, s, t, weight='weight')
    of_path.update(spt)


def reduce(net, names, scores):
    ''' apply the successive steps to reduce the network '''
    print('initial network:')
    print('node nb: {}, edge nb:{}'.format(nx.number_of_nodes(net), nx.number_of_edges(net)))
    compacted, group_names = group_positive(net, names, scores)
    double = double_edge(compacted, scores)
    print('Double edge network:')
    print('node nb: {}, edge nb:{}'.format(nx.number_of_nodes(double), nx.number_of_edges(double)))
    positive = [n for n in double.nodes if scores[n] > 0]
    reduced_nodes = U.through_between(double, positive, keep_nodes, set())
    reduced = double.subgraph(reduced_nodes)
    print('Reduced network:')
    print('node nb: {}, edge nb:{}'.format(nx.number_of_nodes(reduced), nx.number_of_edges(reduced)))
    print()
    return reduced, group_names


if __name__ == "__main__":
    ''' save the reduced network'''
    net_, names_, scores_ = U.load_net(P.files)
    reduced_, group_names_ = reduce(net_, names_, scores_)
    U.display_save(reduced_, names_, scores_, group_names=group_names_, if_edge=True, files=P.rfiles)
