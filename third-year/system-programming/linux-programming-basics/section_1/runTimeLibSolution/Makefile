all: exe

exe: solution.c
	gcc solution.c -Wl,-rpath,. -fPIC -ldl -o solution

lib: multiply_by_2.h multiply_by_2.c
	gcc -shared multiply_by_2.c -fPIC -o libMultiplyBy2.so

clean:
	rm solution *.o libMultiplyBy2.so 2>/dev/null
