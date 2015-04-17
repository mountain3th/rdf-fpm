def split_graph():
	new_graph = open('new_graph.lg', 'w')
	edges = open('edges.lg', 'r')
	vertices = open('vertices.lg', 'r')
	
	save_vertex_line = vertices.readline()
	save_edge_line = edges.readline()
	write_new_grpah(new_graph, 1)
	new_graph.write(save_vertex_line)

	graph_count = 1
	while True:			
		line = vertices.readline()
		save_vertex_line = line
		strings = line.split(' ')
		if not line or cmp(strings[2][:-1], '0') == 0:
			new_graph.write(save_edge_line)
			while True:	
				temp_line = edges.readline()
				if not temp_line:
					return
				temp_strings = temp_line.split(' ')
				vertex1 = temp_strings[1]

				if cmp(vertex1, save_edge_line.split(' ')[1]) != 0:
					save_edge_line = temp_line
					break
				else:
					new_graph.write(temp_line)
			graph_count += 1
			write_new_grpah(new_graph, graph_count)
			new_graph.write(save_vertex_line)
		else:
			if cmp(line[-1], '\n') != 0:
				new_graph.write(line + '\n')
			else:
				new_graph.write(line)

def write_new_grpah(file, graph_count):
	str = '\nt # %d\n' % graph_count
	file.write(str)


if __name__ == '__main__':
	split_graph()