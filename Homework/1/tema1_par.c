#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <pthread.h>

// structura pentru un numar complex
typedef struct _complex {
	double a;
	double b;
} complex;

// structura pentru parametrii unei rulari
typedef struct _params {
	int is_julia, iterations;
	double x_min, x_max, y_min, y_max, resolution;
	complex c_julia;
} params;

char *in_filename_julia;
char *in_filename_mandelbrot;
char *out_filename_julia;
char *out_filename_mandelbrot;
int P;
params par;
int width, height;
int **result;
pthread_barrier_t barrier;

// citeste argumentele programului
void get_args(int argc, char **argv)
{
	if (argc < 6) {
		printf("Numar insuficient de parametri:\n\t"
				"./tema1 fisier_intrare_julia fisier_iesire_julia "
				"fisier_intrare_mandelbrot fisier_iesire_mandelbrot "
				"numar_threaduri\n");
		exit(1);
	}

	in_filename_julia = argv[1];
	out_filename_julia = argv[2];
	in_filename_mandelbrot = argv[3];
	out_filename_mandelbrot = argv[4];
	P = atoi(argv[5]);
}

// citeste fisierul de intrare
void read_input_file(char *in_filename, params* par)
{
	FILE *file = fopen(in_filename, "r");
	if (file == NULL) {
		printf("Eroare la deschiderea fisierului de intrare!\n");
		exit(1);
	}

	fscanf(file, "%d", &par->is_julia);
	fscanf(file, "%lf %lf %lf %lf",
			&par->x_min, &par->x_max, &par->y_min, &par->y_max);
	fscanf(file, "%lf", &par->resolution);
	fscanf(file, "%d", &par->iterations);

	if (par->is_julia) {
		fscanf(file, "%lf %lf", &par->c_julia.a, &par->c_julia.b);
	}

	fclose(file);
}

// scrie rezultatul in fisierul de iesire
void write_output_file(char *out_filename, int **result, int width, int height)
{
	int i, j;

	FILE *file = fopen(out_filename, "w");
	if (file == NULL) {
		printf("Eroare la deschiderea fisierului de iesire!\n");
		return;
	}

	fprintf(file, "P2\n%d %d\n255\n", width, height);
	for (i = 0; i < height; i++) {
		for (j = 0; j < width; j++) {
			fprintf(file, "%d ", result[i][j]);
		}
		fprintf(file, "\n");
	}

	fclose(file);
}

// aloca memorie pentru rezultat
int **allocate_memory(int width, int height)
{
	int **result;
	int i;

	result = malloc(height * sizeof(int*));
	if (result == NULL) {
		printf("Eroare la malloc!\n");
		exit(1);
	}

	for (i = 0; i < height; i++) {
		result[i] = malloc(width * sizeof(int));
		if (result[i] == NULL) {
			printf("Eroare la malloc!\n");
			exit(1);
		}
	}

	return result;
}

// elibereaza memoria alocata
void free_memory(int **result, int height)
{
	int i;

	for (i = 0; i < height; i++) {
		free(result[i]);
	}
	free(result);
}

// ruleaza algoritmul Julia
void run_julia(params *par, int **result, int width, int height, int thread_id)
{
	int w, h, i;
	int range = ceil((double)width / P);
	int start = thread_id * range;
	// stop = min(width - 1, start + range - 1);
	int stop = (width - 1) < (start + range - 1) ? (width - 1) : (start + range - 1);

	for (w = start; w <= stop; w++) {
		for (h = 0; h < height; h++) {
			int step = 0;
			complex z = { .a = w * par->resolution + par->x_min,
							.b = h * par->resolution + par->y_min };

			while (sqrt(pow(z.a, 2.0) + pow(z.b, 2.0)) < 2.0 && step < par->iterations) {
				complex z_aux = { .a = z.a, .b = z.b };

				z.a = pow(z_aux.a, 2) - pow(z_aux.b, 2) + par->c_julia.a;
				z.b = 2 * z_aux.a * z_aux.b + par->c_julia.b;

				step++;
			}

			result[h][w] = step % 256;
		}
	}
	// asteapta toate threadurile
	pthread_barrier_wait(&barrier);

	// transforma rezultatul din coordonate matematice in coordonate ecran
	int nr_lines = height / 2;
	range = ceil((double)nr_lines / P);
	start = thread_id * range;
	// stop = min(nr_lines - 1, start + range - 1);
	stop = (nr_lines - 1) < (start + range - 1) ? (nr_lines - 1) : (start + range - 1);
	
	for (i = start; i <= stop; i++) {
		int *aux = result[i];
		result[i] = result[height - i - 1];
		result[height - i - 1] = aux;
	}
}

// ruleaza algoritmul Mandelbrot
void run_mandelbrot(params *par, int **result, int width, int height, int thread_id)
{
	int w, h, i;
	int range = ceil((double)width / P);
	int start = thread_id * range;
	// stop = min(width - 1, start + range - 1);
	int stop = (width - 1) < (start + range - 1) ? (width - 1) : (start + range - 1);
	
	for (w = start; w <= stop; w++) {
		for (h = 0; h < height; h++) {
			complex c = { .a = w * par->resolution + par->x_min,
							.b = h * par->resolution + par->y_min };
			complex z = { .a = 0, .b = 0 };
			int step = 0;

			while (sqrt(pow(z.a, 2.0) + pow(z.b, 2.0)) < 2.0 && step < par->iterations) {
				complex z_aux = { .a = z.a, .b = z.b };

				z.a = pow(z_aux.a, 2.0) - pow(z_aux.b, 2.0) + c.a;
				z.b = 2.0 * z_aux.a * z_aux.b + c.b;

				step++;
			}

			result[h][w] = step % 256;
		}
	}
	// asteapta toate threadurile
	pthread_barrier_wait(&barrier);

	// transforma rezultatul din coordonate matematice in coordonate ecran
	int nr_lines = height / 2;
	range = ceil((double)nr_lines / P);
	start = thread_id * range;
	// stop = min(nr_lines - 1, start + range - 1);
	stop = (nr_lines - 1) < (start + range - 1) ? (nr_lines - 1) : (start + range - 1);
	
	for (i = start; i <= stop; i++) {
		int *aux = result[i];
		result[i] = result[height - i - 1];
		result[height - i - 1] = aux;
	}
}

// functia executata de fiecare thread
void *thread_function(void *arg)
{
	int thread_id = *(int *)arg;
	// Julia
	if(thread_id == 0)
	{
		// se citesc parametrii de intrare
		// se aloca tabloul cu rezultatul
		read_input_file(in_filename_julia, &par);
		width = (par.x_max - par.x_min) / par.resolution;
		height = (par.y_max - par.y_min) / par.resolution;
		result = allocate_memory(width, height);
	}
	// asteapta toate threadurile
	pthread_barrier_wait(&barrier);
	// se ruleaza algoritmul
	run_julia(&par, result, width, height, thread_id);
	// asteapta toate threadurile
	pthread_barrier_wait(&barrier);
	if(thread_id == 0)
	{
		// se scrie rezultatul in fisierul de iesire
		write_output_file(out_filename_julia, result, width, height);
		// se elibereaza memoria alocata
		free_memory(result, height);
	}
	
	// Mandelbrot
	if(thread_id == 0)
	{
		// se citesc parametrii de intrare
		// se aloca tabloul cu rezultatul
		read_input_file(in_filename_mandelbrot, &par);
		width = (par.x_max - par.x_min) / par.resolution;
		height = (par.y_max - par.y_min) / par.resolution;
		result = allocate_memory(width, height);
	}
	// asteapta toate threadurile
	pthread_barrier_wait(&barrier);
	// se ruleaza algoritmul
	run_mandelbrot(&par, result, width, height, thread_id);
	// asteapta toate threadurile
	pthread_barrier_wait(&barrier);
	if(thread_id == 0)
	{
		// se scrie rezultatul in fisierul de iesire
		write_output_file(out_filename_mandelbrot, result, width, height);
		// se elibereaza memoria alocata
		free_memory(result, height);
	}
	
	pthread_exit(NULL);
}

int main(int argc, char *argv[])
{
	// se citesc argumentele programului
	get_args(argc, argv);
	
	pthread_t thread[P];
	int thread_id[P];
	int r = pthread_barrier_init(&barrier, NULL, P);
	if (r != 0) {
		printf("Eroare la initializarea barierei!\n");
		exit(-1);
	}
	// se creeaza thread-urile
	for (int i = 0; i < P; i++) {
		thread_id[i] = i;
		r = pthread_create(&thread[i], NULL, thread_function, &thread_id[i]);
		if (r != 0) {
			printf("Eroare la crearea threadului!\n");
			exit(-1);
		}
	}
	// se asteapta thread-urile
	for (int i = 0; i < P; i++) {
		r = pthread_join(thread[i], NULL);
		if (r != 0) {
			printf("Eroare la asteptarea threadului!\n");
			exit(-1);
		}
	}
	r = pthread_barrier_destroy(&barrier);
	if (r != 0) {
		printf("Eroare la distrugerea barierei!\n");
		exit(-1);
	}

	return 0;
}
