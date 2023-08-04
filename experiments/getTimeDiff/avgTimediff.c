#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_LINE_NUM 1000

int main(void)
{
    FILE* src = fopen("Timedifference.log","rt");
    char line[MAX_LINE_NUM] = {0,};
    long long num = 0;
    for(int i = 0; i< 100; i++)
    {
        fgets(line,MAX_LINE_NUM,src);
        long long temp = atoll(line);
        num += temp;
    }
    printf("%lld",num/100);
}