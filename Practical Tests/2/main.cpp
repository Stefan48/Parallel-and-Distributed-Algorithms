#include <bits/stdc++.h>
#include <mpi.h>
#include <stdio.h>
#include <math.h>
#include <stdlib.h>

#define MAX_M 1000

using namespace std;

int main(int argc, char * argv[])
{
	int nr_proc;
	int rank;
	MPI_Init(&argc, &argv);
	MPI_Comm_size(MPI_COMM_WORLD, &nr_proc);
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	
	int M;
	double x[MAX_M];
	double coef[nr_proc];
	double c;
	
	// MASTER == 0
	if (rank == 0)
	{
		// M == number of values to apply function on
		cin >> M;
		// read M values
		for (int i = 0; i < M; ++i)
		{
			cin >> x[i];
		}
		// read coefficients
		for (int i = 0; i < nr_proc; ++i)
		{
			cin >> coef[i];
		}
		c = coef[0];
		// send M, x array (M values) and corresponding coefficient to each other process
		for (int j = 1; j < nr_proc; ++j)
		{
			MPI_Send(&M, 1, MPI_INT, j, 0, MPI_COMM_WORLD);
			MPI_Send(x, M, MPI_DOUBLE, j, 0, MPI_COMM_WORLD);
			MPI_Send(&coef[j], 1, MPI_DOUBLE, j, 0, MPI_COMM_WORLD);
		}
	}
	else
	{
		// receive M
		MPI_Recv(&M, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		// receive M values
		MPI_Recv(x, M, MPI_DOUBLE, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		// receive my coefficient
		MPI_Recv(&c, 1, MPI_DOUBLE, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
	}
	
	// array to store my computed values
	double val[MAX_M];
	// array to store received computed values
	double val_recv[MAX_M];
	for (int i = 0; i < M; ++i)
	{
		val[i] = (rank + 1) * pow(x[i], c);
	}
	// all process except Master receive computed values from previous process
	if (rank != 0)
	{
		MPI_Recv(val_recv, M, MPI_DOUBLE, rank - 1, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		// multiply computed values with received values
		for (int i = 0; i < M; ++i)
		{
			val[i] *= val_recv[i];
		}
	}
	// all process except last one send values to next process
	if (rank != nr_proc - 1)
	{
		MPI_Send(val, M, MPI_DOUBLE, rank + 1, 0, MPI_COMM_WORLD);
	}
	else
	{
		// last process prints function's value for all M inputs
		for (int i = 0; i < M; ++i)
			cout << val[i] << " ";
		cout << "\n";
	}
	
	MPI_Finalize();
	return 0;
}
