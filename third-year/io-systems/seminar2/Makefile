PANDOC ?= pandoc
PDF_ENGINE ?= xelatex

.PHONY: presentation clean

presentation: presentation.pdf

presentation.pdf: presentation.md
	$(PANDOC) presentation.md -t beamer --pdf-engine=$(PDF_ENGINE) -o presentation.pdf

clean:
	rm -f presentation.pdf
