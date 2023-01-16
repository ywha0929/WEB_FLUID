#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_LINE 1024
#define MAX_PER_LINE 1024

int main(void)
{
    char RunUpdateLine[MAX_PER_LINE];
    char FrameCompleteLine[MAX_PER_LINE];
    char* eptr;
    char buffer[MAX_PER_LINE];
    long time_diff = 7124931260;
    FILE * RunUpdateFile = fopen("/home/ywha/WEB_FLUID/experiments/UIOverheadTest/pixel5Timestamp.log","rt");
    FILE * FrameCompleteFile = fopen("/home/ywha/WEB_FLUID/experiments/UIOverheadTest/frameComplete.log","rt");

    for(int i = 0; i< 50; i++)
    {
        for(int j= 0; j < 6; j++)
        {

            if (fgets(RunUpdateLine,MAX_LINE,RunUpdateFile) == NULL)
                break;
            
            // printf("%s\n",RunUpdateLine);
            long RunUpdateTime = strtol(RunUpdateLine,&eptr,0);
            fgets(FrameCompleteLine,MAX_LINE,FrameCompleteFile);
            long FrameCompleteTime = strtol(FrameCompleteLine,&eptr,0);
            printf("%ld,",(FrameCompleteTime - RunUpdateTime + time_diff)/ 1000000);
        }
        printf("\n");
    }
}