#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <math.h>
#include "cyclic_vector.h"

int N;
int P;
int C;

int I = 0;
int *result;

struct cyclic_vector *v;

pthread_mutex_t lock_vector;
pthread_mutex_t lockI;
pthread_mutex_t lock_result;


void get_args(int argc, char **argv)
{
	if(argc < 4) {
		printf("Not enough parameters: ./program N P C\n");
		exit(1);
	}

	N = atoi(argv[1]);
	P = atoi(argv[2]);
	C = atoi(argv[3]);
}


void *producer_function(void *arg)
{
	int thread_id = *(int *)arg;
	
	for (int i = 0; i < N; ++i)
	{
		// lock vector
		int r = pthread_mutex_lock(&lock_vector);
		if (r != 0)
		{
			printf("mutex lock error\n");
			exit(-1);
		}
		
		r = put_in_cyclic_vector(v, i);
		if (r != 0)
		{
			// vector full => remain on the current step
			// (wait for consumers to consume)
			i--;
		}
		
		// unlock vector
		r = pthread_mutex_unlock(&lock_vector);
		if (r != 0)
		{
			printf("mutex unlock error\n");
			exit(-1);
		}
	}
	
	printf("Producer %d finished correctly.\n", thread_id);
	
	pthread_exit(NULL);
}

void *consumer_function(void *arg)
{
	int thread_id = *(int *)arg;
	
	while(1)
	{
	
		if (I >= P * N)
		{
			break;
		}
		
		// lock vector
		int r = pthread_mutex_lock(&lock_vector);
		if (r != 0)
		{
			printf("mutex lock error\n");
			exit(-1);
		}
	
		int g = get_from_cyclic_vector(v);
		
		// unlock vector
		r = pthread_mutex_unlock(&lock_vector);
		if (r != 0)
		{
			printf("mutex unlock error\n");
			exit(-1);
		}
		
		if (g != 0x7FFFFFFF)
		{
			// valid value
			
			// lock and increment I
			int r = pthread_mutex_lock(&lockI);
			if (r != 0)
			{
				printf("mutex lock error\n");
				exit(-1);
			}
			I++;
			// unlock I
			r = pthread_mutex_unlock(&lockI);
			if (r != 0)
			{
				printf("mutex unlock error\n");
				exit(-1);
			}
			
			// lock and update result
			r = pthread_mutex_lock(&lock_result);
			if (r != 0)
			{
				printf("mutex lock error\n");
				exit(-1);
			}
			result[g]++;
			// unlock result
			r = pthread_mutex_unlock(&lock_result);
			if (r != 0)
			{
				printf("mutex unlock error\n");
				exit(-1);
			}
			
		}
		
		
	}
		
	printf("Consumer %d finished correctly.\n", thread_id);
	
	pthread_exit(NULL);
}





int main(int argc, char *argv[])
{
	get_args(argc, argv);
	
	v = new_cyclic_vector(5);
	result = (int*)malloc (N * sizeof(int));
	for (int i = 0; i < N; ++i)
		result[i] = 0;
	
	int i, r;

	pthread_t threads[P + C];
	int thread_id[P + C];
	
	
	// init locks
	r = pthread_mutex_init(&lock_vector, NULL);
	if (r != 0)
	{
		printf("mutex init error\n");
		exit(-1);
	}
	r = pthread_mutex_init(&lockI, NULL);
	if (r != 0)
	{
		printf("mutex init error\n");
		exit(-1);
	}
	r = pthread_mutex_init(&lock_result, NULL);
	if (r != 0)
	{
		printf("mutex init error\n");
		exit(-1);
	}

	for (i = 0; i < P + C; i++)
	{
		thread_id[i] = i;
		if (i < P)
		{
			pthread_create(&threads[i], NULL, producer_function, &thread_id[i]);
		}
		else
		{
			pthread_create(&threads[i], NULL, consumer_function, &thread_id[i]);
		}
		
	}

	for (i = 0; i < P + C; i++)
	{
		pthread_join(threads[i], NULL);
	}
	
	
	// check if correct
	int ok = 1;
	for (int i = 0; i < N; ++i)
	{
		if (result[i] != P)
		{
			ok = 0;
			break;
		}
	}
	if (ok)
		printf("CORRECT\n");
	else
		printf("FAILED\n");
	
	
	// destroy locks
	r = pthread_mutex_destroy(&lock_vector);
	if (r != 0)
	{
		printf("mutex destroy error\n");
		exit(-1);
	}
	r = pthread_mutex_destroy(&lockI);
	if (r != 0)
	{
		printf("mutex destroy error\n");
		exit(-1);
	}
	r = pthread_mutex_destroy(&lock_result);
	if (r != 0)
	{
		printf("mutex destroy error\n");
		exit(-1);
	}
	
	delete_cyclic_vector(&v);
	free(result);

	return 0;
}
