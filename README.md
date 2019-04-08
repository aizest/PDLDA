# PDLDA

This repository presents a Java implementation of the Product Defect Latent Dirichlet Allocation Model (`PDLDA`), along with datasets created for this model.

## Model

As an aspect-oriented opinion mining model, `PDLDA` identifies domain-specific knowledge about product issues from online user generated contents (e.g., product forum posts) using interdependent three-dimensional topics: Component, Symptom, and Resolution. Based on them, customers can make purchase decisions, manufacturers can improve products, governments can undertake administrative actions, and patients can find useful information for disease diagnosis and treatment.

Please refer to the following paper for citation information:

> [Xuan Zhang, Zhilei Qiao, Aman Ahuja, Weiguo Fan, Edward Fox, Chandan Reddy, "Discovering Product Defects and Solutions from Online User Generated Contents." Proceedings of the World Wide Web Conference 2019. IW3C2, ACM, 2019.](http://dmkd.cs.vt.edu/papers/WWW19a.pdf)

## Dataset 

We released two datasets on product defect discovery, which are introduced in the WWW 2019 paper above. Both datasets are placed under "workspace/complaints" directory. The posts in them are collected from online forums. 
> * `Macbook-complaints.csv`: The dataset is on Apple MacBook products, collected from discussions.apple.com
> * `Patient-complaints.csv`: The dataset is on human disease, collected from patient.info.

For each complaint (post), we show the basic information such as "title", "question content", and "solution content". The pre-extracted entities, such as "component", "symptom", and "resolution", are supplied for your convenience. We also provid a label to each complaint for entity/document clustering purpose.

## Source Packages
To run this project, just run the "PDLDAOnWordGroups" class under "LDA.console" package. The key packages of this project are shown below.

> `datastructures`
> * Common data strctures for data analysis
>
> `LDA`
> * Console program to run PDLDA model
> * Supporting data structure
> * PDLDA model implementation
>
> `preprocess`
> * Model input preparation
> * Supporting utilities
>
> `utility`
> * General utility classes
>

## Configuration Files
> `PDLDA.config.properties`: to set configuration for PDLDA
> * Location of data files
> * Product name and year
> * Hyper parameters of PDLDA
> * Number of 3 types of topics
> * Number of words shown for each topic
> * Number of iterations
>

## Dictionary Files
These lexicons are generated during the entity extraction process, which may be used by PDLDA.
> * Component word dictionary
> * Symptom word dictionary
> * Resolution word dictionary
> * Stop word dictionary
>

## Libraries Needed
Please download these jar files, place them under a "lib" directory, and add them to the "CLASSPATH" environment variable.

> * json-simple.jar (V1.1.1)
> * lucene-analyzers-common.jar (V4.6.0)
> * opencsv.jar (V3.2)
> * poi.jar (V3.9)
> * poi-ooxml.jar (V3.9)
> * poi-ooxml-schemas.jar (V3.9)
> * stanford-corenlp-models.jar (V3.5.2)
> * stanford-corenlp.jar (V3.5.2)
> * stanford-parser.jar (V3.5.2)
> * stanford-parser-model.jar (V3.5.2)
>

## License

Copyright (c) 2019 Xuan Zhang