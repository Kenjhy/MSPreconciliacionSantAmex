import boto3
import argparse
import time

parser = argparse.ArgumentParser()

parser.add_argument(
    "-f",
    "--file-name",
    dest="file",
    required=True,
    help="Archivo que contiene variables de ejecución"
)

parser.add_argument(
    "-c",
    "--cluster-name",
    dest="cluster",
    required=True,
    help="Nombre del cluster en ECS"
)

parser.add_argument(
    "-s",
    "--service-name",
    dest="serviceName",
    required=True,
    help="Nombre del microservicio"
)

parser.add_argument(
    "-d",
    "--delete-parameters",
    dest="deleteparams",
    required=False,
    action="store_true",
    help="Al agregarse, primero elimina todos los parámetros del servicio antes de crear los del archivo de variable de ejecución"
)
args = parser.parse_args()

fileVariables = args.file
clusterName = args.cluster
serviceName = args.serviceName
deleteParameters = args.deleteparams

session = boto3.Session()
ssm_client = session.client('ssm')

# Se agrega purgado de parámetros
if deleteParameters:
    prefix = f"/{clusterName}/{serviceName}/"


    paginator = ssm_client.get_paginator('describe_parameters')
    response = paginator.paginate(
        ParameterFilters=[
            {
                'Key': 'Name',
                'Option': 'BeginsWith',
                'Values': [
                    prefix,
                ]
            }
        ]
    )

    parametersList = []
    for parameterObtained in response:
        for parameter in enumerate(parameterObtained['Parameters']):
            parametersList.append(parameter['Name'])

    totalParameters = len(parametersList)

    print("Parametros actuales:")
    for param in parametersList:
        print(param)
    print(f"Existen actualmente: {totalParameters} parámetros.")

    print("Se comenzará la eliminación de parámetros...")
    time.sleep(3)
    try:
        response = ssm_client.delete_parameters(Names=parametersList)
        print(f"Se eliminaron los {totalParameters} parametros")
    except Exception as e:
        print(e)
        print(f"Ocurrió un problema al intentar eliminar los parametros")
print("\n")

print("Se agregarán parámetros desde archivo. Por favor espere...")
time.sleep(30)

with open(fileVariables, "r") as file:
    lineas = file.readlines()

for line in lineas:
    if ":" in line and "=" in line:   
        separator = min(line.index(":"), line.index("=")) 
    elif ":" in line:
        separator = line.index(":")
    elif "=" in line:
        separator = line.index("=")
    else:
        continue
    separatorValue = line[separator]
    parameterComponents = line.split(separatorValue, 1)
    parameter = parameterComponents[0].strip()
    parameterValue = parameterComponents[1].strip()
    parameterName="/" + clusterName + "/" + serviceName + "/" + parameter
    try:
        ssm_client.put_parameter(
            Name=parameterName,
            Value=parameterValue,
            Type='SecureString'
        )
        print(f"Se creo el parametro {parameterName}")
    except Exception as e:
        print(e)
print("Se terminaron de agregar los parámetros. Favor de validar en Parameter Store.")
