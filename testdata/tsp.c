
double dist(int num_cities, double *distances, int a, int b);

int *solve_tsp(int num_cities, double *distances) {

	double best_distance = -1;
	int *best_result = 0;

	// will be re-used in each outer-loop iteration
	char *visited = malloc(num_cities * 1);

    for (int start = 0; start < num_cities; start++) {

        for (int i = 0; i < num_cities; i++) {
			visited[i] = 0;
		}
		visited[start] = 1;

		int *result = malloc(num_cities * 4);

		result[0] = start;

		double distance = 0;

        for (int i = 1; i < num_cities; i++) {
			// find nearest neighbor

			int nn = -1;
			double nd = -1;
			for (int j = 0; j < num_cities; j++) {
				if (!visited[j] && (nd < 0 || dist(num_cities, distances, result[i - 1], j) < nd)) {
					nn = j;
					nd = dist(num_cities, distances, result[i - 1], j);
				}
			}

			visited[nn] = 1;
			result[i] = nn;

			distance = distance + dist(num_cities, distances, result[i - 1], nn);
		}

		if (best_distance < 0 || best_distance > distance) {
			best_distance = distance;
			if (best_result != 0) {
				free(best_result);
			}
			best_result = result;
		} else {
			free(result);
		}
	}

	free(visited);

	return best_result;
}
