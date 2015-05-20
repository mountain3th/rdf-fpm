#coding=utf-8

from conf import *
import sys, getopt
import linecache
import os.path
import time

predicate_mapping_txt = ''
objs_mapping_txt = ''
subs_mapping_txt = ''
entities_type_txt = ''
types_mapping_txt = ''

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

def add_node2(vertices, index, labels):
	string = 'v %d %s\n' % (index, labels)
	vertices.append(string)	

def add_edge2(edges, sub, obj, label):
	string = 'e %d %d %d\n' % (sub, obj, label)
	edges.append(string)

def combine2(output_file, vertices, edges, graph_count):
	graph = open(output_file, 'a')
	graph.write('\nt # %d\n' % graph_count)
	for v in vertices:
		graph.write(v)
	for e in edges:
		graph.write(e)
	del vertices[:]
	del edges[:]

	graph.close()


def preprocess_types(input_file):
	if not os.path.exists(types_mapping_txt):
		objs = set()
		with open(input_file) as instances:
			for line in instances:
				string = line.split()
				objs.add(string[2])
		objs_list = list(objs)
		objs_list.sort()

		with open(types_mapping_txt, 'w') as types_mapping:
			for item in objs_list:
				types_mapping.write(item + '\n')

	if not os.path.exists(entities_type_txt):
		types_mapping_count = count_lines(types_mapping_txt)
		count = count_lines(input_file)

		with open(entities_type_txt, 'w') as types:
			with open(input_file) as instances:
				sub_now = ''
				types_list = list()
				for lines_count, line in enumerate(instances):
					strings = line.split()
					sub = strings[0]
					obj = strings[2]
					if cmp(sub_now, sub) != 0:
						if sub_now.strip():
							labels = ','.join(str(i) for i in types_list)
							string = '%s %s\n' % (sub_now, labels)
							types.write(string)
							print lines_count, str(round(float(lines_count) / float(count) * 100, 2)) + '%'
						sub_now = sub
						del types_list[:]
					types_list.append(find(types_mapping_txt, obj, types_mapping_count))
		sort(entities_type_txt)

def sort(file):
	lines = open(file).readlines()
	lines.sort()
	with open(file, 'w') as types:
		for line in lines:
			types.write(line)

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

def find2(file, string, lines):
	i, j = 1, lines
	while i <= j:
		ln = (i + j) / 2
		line = linecache.getline(file, ln).split()
		labels = line[1][-1]
		print ln, string, line
		ret = cmp(string, line[0])
		if ret == 0:
			return labels
		elif ret < 0:
			j = ln - 1
		else:
			i = ln + 1
	return -1

def gen(lines_count, input_file, output_file):
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
	for i in predicates:
		if i in ['<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>', 'http://dbpedia.org/ontology/type']:
			concept_types_labels.add(i + 1)
	with open(tmp_file_txt, 'w') as tmp_file:
		for label in concept_types_labels:
			tmp_file.write(str(label) + '\n')

def gen_types(mapping_file, output_file):

	types_file_lines = count_lines(entities_type_txt)
	lines_count = count_lines(mapping_file)

	predicates = open(predicate_mapping_txt).readlines()
	predicates = [p[:-1] for p in predicates]

	vertices = []
	edges = []
	with open(mapping_file) as mapping:
		graph_count = -1
		index = -1
		subject = ''
		for count, line in enumerate(mapping):
			strings = line.split()
			subject_now = strings[0]
			predicate = strings[1]
			object_now = strings[2]

			print subject_now, object_now

			pre = predicates.index(predicate) + 1
			obj = find2(entities_type_txt, object_now, types_file_lines)
			
			if obj < 0:
				open('error.log', 'a').write(object_now + '\n')
			if cmp(subject, subject_now) != 0:
				if vertices:
					time.sleep(3)
					graph_count += 1
					combine2(output_file, vertices, edges, graph_count)
					index = 0	 
					print graph_count, str(round(float(count) / float(lines_count) * 100, 2)) + '%'
				sub = find2(entities_type_txt, subject_now, types_file_lines)
				if sub < 0: 
					open('error.log', 'a').write(subject_now + '\n')
				add_node2(vertices, 0, sub)
				subject = subject_now
			
			index += 1
			add_node2(vertices, index, obj)
			add_edge2(edges, 0, index, pre)
			
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
	
	name = output_file.split('.')[0] + "_"
	subs_mapping_txt = name + subs_mapping_txt_suffix
	objs_mapping_txt = name + objs_mapping_txt_suffix
	predicate_mapping_txt = name + predicate_mapping_txt_suffix
	entities_type_txt = name + entities_type_txt_suffix
	types_mapping_txt = name + types_mapping_txt_suffix
	type_output_txt = name + type_output_txt_suffix
		
	if not os.path.exists(output_file):
		lines_count = count_lines(input_file)
		gen(lines_count, input_file, output_file)
	if not os.path.exists(type_output_txt):
		preprocess_types('instance_types_en.ttl')
		gen_types(input_file, type_output_txt)
