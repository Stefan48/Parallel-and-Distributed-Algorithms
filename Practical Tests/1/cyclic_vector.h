struct cyclic_vector {
	int head;
	int tail;
	int size;
	int capacity;
	int* array;
};

struct cyclic_vector* new_cyclic_vector(int s);

void delete_cyclic_vector(struct cyclic_vector** v);

int put_in_cyclic_vector(struct cyclic_vector* v, int x);

int get_from_cyclic_vector(struct cyclic_vector* v);

void print_cyclic_vector(struct cyclic_vector* v);
