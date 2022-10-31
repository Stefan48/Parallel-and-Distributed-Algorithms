#include <bits/stdc++.h>
#include <unistd.h>
#include <mpi.h>

#define MAX_PARAGRAPH_SIZE 1000000

using namespace std;

struct Paragraph
{
	int index;
	int length;
	int nr_lines;
	char *text;
	
	Paragraph()
	{
		this->index = 0;
		this->length = 0;
		this->nr_lines = 0;
		this->text = NULL;
	}
	
	Paragraph(int index, int length, int nr_lines, char *text)
	{
		this->index = index;
		this->length = length;
		this->nr_lines = nr_lines;
		this->text = text;
	}
};

struct MasterThreadParams
{
	int thread_id;
	vector<Paragraph> *final_paragraphs;
};

struct WorkerThreadParams
{
	int worker_id;
	int thread_id;
	int nr_cores;
	int nr_paragraphs;
	vector<Paragraph> *paragraphs;
	vector<Paragraph> *edited_paragraphs;
	vector<char> *new_paragraph;
};

string input_file;
string output_file;

pthread_barrier_t workers_barrier;
pthread_mutex_t mutex_lock;

int GetGenreCode(string genre)
{
	if (genre == "horror")
		return 1;
	else if (genre == "comedy")
		return 2;
	else if (genre == "fantasy")
		return 3;
	else if (genre == "science-fiction")
		return 4;
	return -1;
}
string GetGenreName(int code)
{
	switch (code)
	{
		case 1: return "horror";
		case 2: return "comedy";
		case 3: return "fantasy";
		case 4: return "science-fiction";
		default: return "unknown genre";
	}
}

void *MasterThreadFunction(void *arg)
{
	MasterThreadParams params = *(MasterThreadParams*)arg;
	int thread_id = params.thread_id;
	vector<Paragraph> *final_paragraphs = params.final_paragraphs;
	vector<Paragraph> paragraphs;
	// parse input file
	ifstream in(input_file);
	string genre;
	char paragraph[MAX_PARAGRAPH_SIZE];
	char *text;
	int index = 0;
	int length;
	int nr_lines;
	char c;
	bool stop = false;
	while (!stop)
	{
		in >> genre;
		// read '\n' and dump it
		in.get(c);
			
		length = 0;
		nr_lines = 0;
		while (true)
		{
			in.get(c);
			if (in.eof())
			{
				stop = true;
				break;
			}
			paragraph[length++] = c;
			if (paragraph[length - 1] == '\n')
			{
				if (paragraph[length - 2] == '\n')
				{
					break;
				}
				nr_lines++;
			}
		}
		if (length > 0)
		{
			if (GetGenreCode(genre) == thread_id)
			{
				text = new char[length];
				strncpy(text, paragraph, length);
				paragraphs.push_back(Paragraph(index, length, max(nr_lines, 1), text));
			}
			index++;
		}
	}
	in.close();
	// send paragraphs to correspondent worker and free allocated memory
	// send number of paragraphs
	int nr_paragraphs = paragraphs.size();
	MPI_Send(&nr_paragraphs, 1, MPI_INT, thread_id, 0, MPI_COMM_WORLD);
	// send paragraphs
	for (int i = 0; i < nr_paragraphs; ++i)
	{
		// send index
		MPI_Send(&paragraphs[i].index, 1, MPI_INT, thread_id, 0, MPI_COMM_WORLD);
		// send length
		MPI_Send(&paragraphs[i].length, 1, MPI_INT, thread_id, 0, MPI_COMM_WORLD);
		// send number of lines
		MPI_Send(&paragraphs[i].nr_lines, 1, MPI_INT, thread_id, 0, MPI_COMM_WORLD);
		// send text
		MPI_Send(paragraphs[i].text, paragraphs[i].length, MPI_CHAR, thread_id, 0, MPI_COMM_WORLD);
		// free text memory
		delete paragraphs[i].text;
	}
	
	int ret;
	// receive edited paragraphs from correspondent worker
	for (int i = 0; i < nr_paragraphs; ++i)
	{
		// receive length
		MPI_Recv(&length, 1, MPI_INT, thread_id, thread_id, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		paragraphs[i].length = length;
		// receive text
		paragraphs[i].text = new char[length];
		MPI_Recv(paragraphs[i].text, length, MPI_CHAR, thread_id, thread_id, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		// save to final paragraphs vector
		// replace nr_lines with thread_id in order to know the genre
		ret = pthread_mutex_lock(&mutex_lock);
		if (ret)
		{
			exit(-1);
		}
		final_paragraphs->push_back(Paragraph(paragraphs[i].index, length, thread_id, paragraphs[i].text));
		ret = pthread_mutex_unlock(&mutex_lock);
		if (ret)
		{
			exit(-1);
		}
	}
	pthread_exit(NULL);
}

bool isConsonant(char c)
{
	return (strchr("bcdfghjklmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ", c) != NULL);
}

void *WorkerThreadFunction(void *arg)
{
	WorkerThreadParams params = *(WorkerThreadParams*)arg;
	int worker_id = params.worker_id;
	int thread_id = params.thread_id;
	int nr_cores = params.nr_cores;
	int nr_paragraphs = params.nr_paragraphs;
	vector<Paragraph> *paragraphs = params.paragraphs;
	vector<Paragraph> *edited_paragraphs = params.edited_paragraphs;
	vector<char> *new_paragraph = params.new_paragraph;
	
	if (thread_id == 0)
	{
		// thread 0 receives paragraphs from master
		int index;
		int length;
		int nr_lines;
		char *text;
		for (int i = 0; i < nr_paragraphs; ++i)
		{
			// receive index
			MPI_Recv(&index, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
			// receive length
			MPI_Recv(&length, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
			// receive number of lines
			MPI_Recv(&nr_lines, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
			// receive text
			text = new char[length];
			MPI_Recv(text, length, MPI_CHAR, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
			paragraphs->push_back(Paragraph(index, length, nr_lines, text));
		}
		// send edited paragraphs to master
		for (int i = 0; i < nr_paragraphs; ++i)
		{
			while (edited_paragraphs->size() < i + 1)
			{
				// wait for the other threads to finish editing the paragraph
				std::this_thread::sleep_for(std::chrono::milliseconds(1));
			}
			// send length
			MPI_Send(&(*edited_paragraphs)[i].length, 1, MPI_INT, 0, worker_id, MPI_COMM_WORLD);
			// send text
			MPI_Send((*edited_paragraphs)[i].text, (*edited_paragraphs)[i].length, MPI_CHAR, 0, worker_id, MPI_COMM_WORLD);
			// free text memory
			delete (*edited_paragraphs)[i].text;
		}
	}
	else
	{
		// other threads process the text
		int processing_cores;
		int index, length, nr_lines;
		int range, start, stop;
		vector<char> edited;
		char c;
		int ret;
		
		for (int i = 0; i < nr_paragraphs; ++i)
		{
			while (paragraphs->size() < i + 1)
			{
				// wait for thread 0 to receive the next paragraph
				std::this_thread::sleep_for(std::chrono::milliseconds(1));
			}
			index = (*paragraphs)[i].index;
			length = (*paragraphs)[i].length;
			nr_lines = (*paragraphs)[i].nr_lines;
			processing_cores = nr_lines / 20;
			if (nr_lines % 20 != 0)
			{
				processing_cores++;
			}
			processing_cores = min(processing_cores, nr_cores - 1);
			if (thread_id <= processing_cores)
			{
				range = ceil((double)length / processing_cores);
				start = (thread_id - 1) * range;
				stop = min(length - 1, start + range - 1);
				
				if (worker_id == 1)
				{
					// process horror paragraph
					for (int j = start; j <= stop; ++j)
					{
						c = (*paragraphs)[i].text[j];
						edited.push_back(c);
						if (isConsonant(c))
						{
							if (c <= 'Z')
							{
								edited.push_back(c + 32);
							}
							else
							{
								edited.push_back(c);
							}
						}
					}
				}
				else if (worker_id == 2)
				{
					// process comedy paragraph
					int j = start, count = 1;
					if (start > 0)
					{
						while (j <= stop && (*paragraphs)[i].text[j-1] != ' ' && (*paragraphs)[i].text[j-1] != '\n')
						{
							j++;
						}
					}
					// if blank character not found, it means the previous thread had already processed this part of the paragraph
					if (j <= stop)
					{
						bool stop_at_next_blank = false;
						while (true)
						{
							if (j == stop)
							{
								stop_at_next_blank = true;
							}
							else if (j >= length)
							{
								break;
							}
							c = (*paragraphs)[i].text[j];
							if (c == ' ' || c == '\n')
							{
								count = 1;
								edited.push_back(c);
								if (stop_at_next_blank)
								{
									break;
								}
							}
							else if (count == 1)
							{
								edited.push_back(c);
								count = 0;
							}
							else
							{
								if (isalpha(c) && c >= 'a')
								{
									edited.push_back(c - 32);
								}
								else
								{
									edited.push_back(c);
								}
								count = 1;
							}
							j++;
						}
					}
				}
				else if (worker_id == 3)
				{
					// process fantasy paragraph
					for (int j = start; j <= stop; ++j)
					{
						c = (*paragraphs)[i].text[j];
						if (isalpha(c) && c >= 'a' && (j == 0 || (*paragraphs)[i].text[j-1] == ' ' || (*paragraphs)[i].text[j-1] == '\n'))
						{
							edited.push_back(c - 32);
						}
						else
						{
							edited.push_back(c);
						}
					}
				}
				else if (worker_id == 4)
				{
					// process science-fiction paragraph
					int j = start, count = 1;
					if (start > 0)
					{
						while (j <= stop && (*paragraphs)[i].text[j-1] != '\n')
						{
							j++;
						}
					}
					// if '\n' not found, it means the previous thread had already processed this part of the paragraph
					if (j <= stop)
					{
						bool stop_at_newline = false;
						vector<char> word;
						while (true)
						{
							if (j == stop)
							{
								stop_at_newline = true;
							}
							else if (j >= length)
							{
								for (int l = word.size() - 1; l >= 0; --l)
								{
									edited.push_back(word[l]);
								}
								break;
							}
							c = (*paragraphs)[i].text[j];
							if (c == ' ')
							{
								count++;
								if (count > 7)
								{
									count = 1;
									for (int l = word.size() - 1; l >= 0; --l)
									{
										edited.push_back(word[l]);
									}
									word.clear();
								}
								edited.push_back(c);
							}
							else if (c == '\n')
							{
								if (count == 7)
								{
									for (int l = word.size() - 1; l >= 0; --l)
									{
										edited.push_back(word[l]);
									}
									word.clear();
								}
								count = 1;
								edited.push_back(c);
								if (stop_at_newline)
								{
									break;
								}
							}
							else if (count == 7)
							{
								word.push_back(c);
							}
							else
							{
								edited.push_back(c);
							}
							j++;
						}
					}
				}	
			}
			// thread 1 is responsible for merging the edited parts
			if (thread_id == 1)
			{
				new_paragraph->clear();
				for (int j = 0; j < edited.size(); ++j)
				{
					new_paragraph->push_back(edited[j]);
				}
				edited.clear();
			}
			pthread_barrier_wait(&workers_barrier);
			for (int t = 2; t <= processing_cores; ++t)
			{
				if (thread_id == t)
				{
					for (int j = 0; j < edited.size(); ++j)
					{
						new_paragraph->push_back(edited[j]);
					}
					edited.clear();
				}
				pthread_barrier_wait(&workers_barrier);
			}
			if (thread_id == 1)
			{
				char *text = new char[new_paragraph->size()];
				for (int j = 0; j < new_paragraph->size(); ++j)
				{
					text[j] = (*new_paragraph)[j];
				}
				edited_paragraphs->push_back(Paragraph(index, new_paragraph->size(), nr_lines, text));
			}
			pthread_barrier_wait(&workers_barrier);
		}
	}
	pthread_exit(NULL);
}

bool cmpParagraphs(Paragraph p1, Paragraph p2)
{
	return p1.index < p2.index;
}

int main(int argc, char *argv[])
{
	int nr_processes;
	int rank;
	int provided;

	MPI_Init_thread(&argc, &argv, MPI_THREAD_MULTIPLE, &provided);
	MPI_Comm_size(MPI_COMM_WORLD, &nr_processes);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    if (nr_processes != 5)
    {
    	exit(-1);
    }
    
    input_file = argv[1];
	output_file = input_file.substr(0, input_file.length() - strlen(".txt")) + ".out";
	
	int ret = pthread_mutex_init(&mutex_lock, NULL);
	if (ret)
	{
		exit(-1);
	}
	if (rank == 0)
	{
		// MASTER
		pthread_t threads[4];
		MasterThreadParams params[4];
		vector<Paragraph> final_paragraphs;
		
		for (int i = 0; i < 4; ++i)
		{
			params[i].thread_id = i + 1;
			params[i].final_paragraphs = &final_paragraphs;
			ret = pthread_create(&threads[i], NULL, MasterThreadFunction, (void*)&params[i]);
			if (ret)
			{
		        exit(-1);
		    }
		}
		for (int i = 0; i < 4; ++i)
		{
        	ret = pthread_join(threads[i], NULL);
		    if (ret)
		    {
		    	exit(-1);
		    }
		}
		
		// sort edited paragraphs by index
		sort(final_paragraphs.begin(), final_paragraphs.end(), cmpParagraphs);
		// print processed text
		ofstream out(output_file);
		for (int i = 0; i < final_paragraphs.size(); ++i)
		{
			out << GetGenreName(final_paragraphs[i].nr_lines) << '\n';
			for (int j = 0; j < final_paragraphs[i].length; ++j)
				out << final_paragraphs[i].text[j];
			delete final_paragraphs[i].text;
		}
		out.close();
	}
	else
	{
		// WORKER
		int nr_cores = sysconf(_SC_NPROCESSORS_CONF);
		pthread_t threads[nr_cores];
		ret = pthread_barrier_init(&workers_barrier, NULL, nr_cores - 1);
		if (ret)
		{
			exit(-1);
		}
		WorkerThreadParams params[nr_cores];
		int nr_paragraphs;
		MPI_Recv(&nr_paragraphs, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, NULL);
		vector<Paragraph> paragraphs, edited_paragraphs;
		vector<char> new_paragraph;
		
		for (int i = 0; i < nr_cores; ++i)
		{
			params[i].worker_id = rank;
			params[i].thread_id = i;
			params[i].nr_cores = nr_cores;
			params[i].nr_paragraphs = nr_paragraphs;
			params[i].paragraphs = &paragraphs;
			params[i].edited_paragraphs = &edited_paragraphs;
			params[i].new_paragraph = &new_paragraph;
			
			ret = pthread_create(&threads[i], NULL, WorkerThreadFunction, (void*)&params[i]);
			if (ret)
			{
		        exit(-1);
		    }
		}
		for (int i = 0; i < nr_cores; ++i)
		{
        	ret = pthread_join(threads[i], NULL);
		    if (ret)
		    {
		    	exit(-1);
		    }
		}
		ret = pthread_barrier_destroy(&workers_barrier);
		if (ret)
		{
			exit(-1);
		}
	}
	ret = pthread_mutex_destroy(&mutex_lock);
	if (ret)
	{
		exit(-1);
	}
	MPI_Finalize();
	return 0;
}
