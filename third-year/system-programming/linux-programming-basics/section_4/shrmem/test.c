#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <sys/ipc.h>
#include <sys/shm.h>

#define SHM_BUF_SIZE_BYTES 1000
#define errExit( msg )    do { perror(msg); exit(EXIT_FAILURE);} while (0)

int main( int aArgc, char **apArgv )
{
   int key1 = ftok( apArgv[ 0 ], getpid() );
   int key2 = ftok( apArgv[ 0 ], getpid() + 1 );

   printf( "%d %d\n", key1, key2 );

   const int shmId1 = shmget( key1, SHM_BUF_SIZE_BYTES, 0666 | IPC_CREAT );
   const int shmId2 = shmget( key2, SHM_BUF_SIZE_BYTES, 0666 | IPC_CREAT );

   /* Attach shared memory into our address space */
   int *pAddr1 = shmat( shmId1, NULL, 0 );
   if ( pAddr1 == (void *) -1 )
   {
      errExit( "shmat1" );
   }

   int *pAddr2 = shmat( shmId2, NULL, 0 );
   if ( pAddr2 == (void *) -1 )
   {
      errExit( "shmat2" );
   }

   int i;
   for ( i = 0; i < 100; i++ )
   {
      pAddr1[ i ] = i;
      pAddr2[ i ] = 100 - i;
   }

   getc( stdin );

   return EXIT_SUCCESS;
}
