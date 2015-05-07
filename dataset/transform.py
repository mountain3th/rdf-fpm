#coding=utf-8

from conf import *
import sys, getopt
import linecache
import os.path

def add_node(vertices, index, label):
	string = 'v %d %d\n' % (index, label)
	vertices.append(string)	

def add_edge(edges, sub, obj, label):
	string = 'e %d %d %d\n' % (sub, obj, label)
	edges.append(string)

def combine(output_file, vertices, edges, graph_count):
	new_graph = open(output_file, 'a')
	new_graph.write('\nt # %d\n' % graph_count)
	for v in vertices:
		new_graph.write(v)
	for e in edges:
		new_graph.write(e)
	del vertices[:]
	del edges[:]

def preprocess(input_file):
	objs = set()
	predicates = set()
	with open(input_file) as mapping:
		for line in mapping:
			string = line.split()
			predicates.add(string[1])
			objs.add(string[2])
			
	predicates_list = list(predicates)
	predicates_list.sort()
	with open(predicate_mapping_txt, 'w') as predicate_mapping:
		for i in predicates_list:
			predicate_mapping.write(i + '\n')

	objs_list = list(objs)
	objs_list.sort()
	with open(objs_mapping_txt, 'w') as objs_mapping:
		for i in objs_list:
			objs_mapping.write(i + '\n')

def find(file, string, lines):
	i, j = 1, lines
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

def main(lines_count, input_file, output_file):
	subs_maps = open(subs_mapping_txt, 'w')
	concept_types_labels = set()

	preprocess(input_file)
	objs_mapping_lines = count_lines(objs_mapping_txt)

	predicates = open(predicate_mapping_txt).readlines()
	predicates = [p[:-1] for p in predicates]

	vertices = []
	edges = []
	with open(input_file) as mapping:
		graph_count = -1
		index = -1
		subject = ''
		for count, line in enumerate(mapping):
			strings = line.split()
			subject_now = strings[0]
			predicate = strings[1]

			pre = predicates.index(predicate) + 1
			if 'type' in predicate:
				concept_types_labels.add(pre)

			obj = find(objs_mapping_txt, strings[2], objs_mapping_lines)
			if obj < 0:
				open('error.log', 'a').write(strings[2] + '\n')
				return

			if cmp(subject, subject_now) != 0:
				if vertices :
					graph_count += 1
					combine(output_file, vertices, edges, graph_count)
					index = 0	 
					print graph_count, str(round(float(count) / float(lines_count) * 100, 2)) + '%'
				add_node(vertices, 0, 0)
				subject = subject_now
				subs_maps.write(subject + '\n')
			
			index += 1
			add_node(vertices, index, obj)
			add_edge(edges, 0, index, pre)

	tmp_file_txt = output_file.split('.')[0] + ".tmp"
	print tmp_file_txt
	with open(tmp_file_txt, 'w') as tmp_file:
		for label in concept_types_labels:
			tmp_file.write(str(label) + '\n')
			
def count_lines(input_file):
	count = 0
	with open(input_file, 'rU') as mapping:
		for count, item in enumerate(mapping):
			pass
	return count + 1

def usage():
	print 'Usage: python transform.py [options]'
	print '       -i\tinputfile(must be rdf triple file)'
	print '       -o\toutputfile'

if __name__ == '__main__':
	opts, args = getopt.getopt(sys.argv[1:], "hi:o:")
	input_file = ""
	output_file = ""
	for op, value in opts:
		if op == '-i':
			input_file = value
		elif op == '-o':
			output_file = value
		elif op == '-h':
			usage()
			sys.exit(1)
	if not os.path.exists(output_file):
		lines_count = count_lines(input_file)
		main(lines_count, input_file, output_file)
