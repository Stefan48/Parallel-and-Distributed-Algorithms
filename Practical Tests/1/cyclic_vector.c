#include <stdlib.h>
#include <stdio.h>
#include "cyclic_vector.h"

struct cyclic_vector* new_cyclic_vector(int s) {
	struct cyclic_vector* v = (struct cyclic_vector*)malloc(sizeof(struct cyclic_vector));
	v->size = s;
	v->capacity = 0;
	v->head = 0;
	v->tail = 0;
	v->array = (int*)malloc(s * sizeof(int));
	return v;
}

void delete_cyclic_vector(struct cyclic_vector** v) {
	free((*v)->array);
	free(*v);
	*v = NULL;
}

int put_in_cyclic_vector(struct cyclic_vector* v, int x) {
	if (v->capacity != v->size) {
		v->array[v->tail] = x;
		v->tail = (v->tail + 1) % v->size;
		v->capacity++;
		return 0;
	}
	return 0x7FFFFFFF;
}

int get_from_cyclic_vector(struct cyclic_vector* v) {
	if (v->capacity != 0) {
		int result = v->array[v->head];
		v->head = (v->head + 1) % v->size;
		v->capacity--;
		return result;
	}
	return 0x7FFFFFFF;
}

void print_cyclic_vector(struct cyclic_vector* v) {
	int i = v->head;
	//printf("head %d tail %d \n", v.head, v.tail);
	int ok = (v->capacity != 0);
	for (; ok || i != v->tail; i = (i+1) % v->size) {
		printf("%d ", v->array[i]);
		ok = 0;
	}
	printf("\n");
}
