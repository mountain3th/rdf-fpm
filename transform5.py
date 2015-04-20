import linecache
import time

def add_node(vertices, index, label):
	string = 'v %d %d\n' % (index, label)
	vertices.append(string)	

def add_edge(edges, sub, obj, label):
	string = 'e %d %d %d\n' % (sub, obj, label)
	edges.append(string)

def combine(vertices, edges, graph_count):
	new_graph = open('new_graph.lg', 'a')
	new_graph.write('\nt # %d\n' % graph_count)
	for v in vertices:
		new_graph.write(v)
	for e in edges:
		new_graph.write(e)
	del vertices[:]
	del edges[:]

def preprocess():
	objs = set()
	with open('mappingbased_properties_en.ttl') as mapping:
		for line in mapping:
			string = line.split(' ')[2][1:-1]
			objs.add(string)
	objs_list = list(objs)
	objs_list.sort()
	with open('objs_mapping.txt', 'w') as objs_mapping:
		for i in objs_list:
			objs_mapping.write(i + '\n')
	return objs_list

def find(file, string):
	i, j = 1, 2260436
	while i <= j:
		ln = (i + j) / 2
		line = linecache.getline(file, ln)[:-1]
		ret = cmp(string, line)
		if ret == 0:
			return ln
		elif ret < 0:
			j = ln - 1
		else:
			i = ln + 1
	return -1

def main(count_lines):
	predicates = open('predicate.txt').readlines()
	predicates = [p[:-1] for p in predicates]
	
	subs_maps = open('subs_mapping.txt', 'w')
	# objs_maps = preprocess()
	
	vertices = []
	edges = []
	with open('mappingbased_properties_en.ttl') as mapping:
		graph_count = -1
		index = -1
		subject = ''
		for count, line in enumerate(mapping):
			strings = line.split(' ')
			subject_now = strings[0][1:-1]
			pre = predicates.index(strings[1][1:-1]) + 1
			# obj = objs_maps.index(strings[2][1:-1]) + 1
			obj = find('objs_mapping.txt', strings[2][1:-1])
			if obj < 0:
				open('error.log', 'a').write(strings[2][1:-1] + '\n')
				return

			if cmp(subject, subject_now) != 0:
				if vertices :
					graph_count += 1
					combine(vertices, edges, graph_count)
					index = 0	 
					print graph_count, str(round(float(count) / float(count_lines) * 100, 2)) + '%'
				add_node(vertices, 0, 0)
				subject = subject_now
				subs_maps.write(subject)
			
			index += 1
			add_node(vertices, index, obj)
			add_edge(edges, 0, index, pre)
			

if __name__ == '__main__':
	main(15229844)
