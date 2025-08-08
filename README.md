# logex-exporter

logex-exporter is a Prometheus Exporter designed to parse and extract metrics from various log files in real-time. It allows users to define log patterns and specify metric types (Counter, Gauge, Histogram, Summary) through flexible configurations, transforming unstructured log data into observable Prometheus metrics.

## Features

- **Real-time Log Tailoring**: Continuously monitors specified log files for changes.
- **Flexible Log Parsing**: Utilizes Grok-like patterns to parse log lines and extract key data.
- **Multiple Metric Types**: Supports Prometheus metric types including Counter, Gauge, Histogram, and Summary.
- **Flexible Labeling**: Dynamically generates and manages metric labels based on log fields, allowing for fine-grained monitoring while optimizing memory and performance.

## Configuration

For detailed configuration, please refer to [config.yml](config.yml).


## Quick Start

1.  **Build jar**:

    ```bash
    mvn clean package
    ```

2.  **Build binary (experimental)**:

    ```bash
    mvn clean package -Pnative
    ```

    By default, the Exporter exposes metrics on port `9090`. You can change this port by modifying the `port` parameter in `config.yml`.
## Contributing

Contributions are welcome! If you have any issues or suggestions, please feel free to open an Issue or submit a Pull Request.